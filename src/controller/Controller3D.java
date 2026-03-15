package controller;

import model.Arrow;
import model.Cube;
import model.Cylinder;
import model.Solid;
import model.Sphere;
import model.Vertex;
import raster.ZBuffer;
import rasterize.LineRasterizerGraphics;
import rasterize.TriangleRasterizer;
import renderer.RendererSolid;
import transforms.Mat4;
import transforms.Mat4PerspRH;
import transforms.Mat4RotY;
import transforms.Mat4Scale;
import transforms.Mat4Transl;
import transforms.Mat4ViewRH;
import transforms.Vec3D;
import view.Panel;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;


public class Controller3D {
    private static final double CAMERA_MOVE_STEP = 0.15;
    private static final double MOUSE_SENSITIVITY = 0.004;
    private static final double PITCH_LIMIT = Math.toRadians(85.0);

    private final Panel panel;
    private final ZBuffer zBuffer;
    private final TriangleRasterizer triangleRasterizer;
    private final RendererSolid rendererSolid;

    private final List<Solid> solids;
    private final double[] solidRotY;
    private final double[] solidScale;
    private final double[] solidMoveX;
    private final double[] solidMoveY;
    private final double[] solidMoveZ;

    private int selectedSolidIndex = 0;
    private Vec3D cameraPosition = new Vec3D(0.0, 1.5, 4.0);
    private double cameraYaw = 0.0;
    private double cameraPitch = 0.0;
    private Integer lastMouseX = null;
    private Integer lastMouseY = null;
    private boolean leftMouseDown = false;

    private Mat4 viewMatrix;
    private Mat4 projectionMatrix;


    public Controller3D(Panel panel) {
        this.panel = panel;
        this.zBuffer = new ZBuffer(panel.getRaster());
        this.triangleRasterizer = new TriangleRasterizer(zBuffer);
        this.rendererSolid = new RendererSolid(
                new LineRasterizerGraphics(panel.getRaster()),
                triangleRasterizer,
                panel.getRaster().getWidth(),
                panel.getRaster().getHeight()
        );

        this.solids = Arrays.asList(new Arrow(), new Sphere(), new Cube(), new Cylinder());
        this.solidRotY = new double[solids.size()];
        this.solidScale = new double[solids.size()];
        this.solidMoveX = new double[solids.size()];
        this.solidMoveY = new double[solids.size()];
        this.solidMoveZ = new double[solids.size()];
        Arrays.fill(solidScale, 1.0);

        updateViewProjectionMatrices();


        initListeners();

        drawScene();
    }


    private void initListeners() {

        panel.setFocusable(true);
        panel.requestFocusInWindow();
        panel.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                boolean redraw = true;

                switch (e.getKeyCode()) {
                    case KeyEvent.VK_TAB:
                        selectedSolidIndex = (selectedSolidIndex + 1) % solids.size();
                        break;
                    case KeyEvent.VK_1:
                    case KeyEvent.VK_2:
                    case KeyEvent.VK_3:
                    case KeyEvent.VK_4:
                        selectedSolidIndex = Math.min(solids.size() - 1, e.getKeyCode() - KeyEvent.VK_1);
                        break;

                    case KeyEvent.VK_Q:
                        solidRotY[selectedSolidIndex] -= 0.1;
                        break;
                    case KeyEvent.VK_E:
                        solidRotY[selectedSolidIndex] += 0.1;
                        break;

                    case KeyEvent.VK_LEFT:
                        solidMoveX[selectedSolidIndex] -= 0.1;
                        break;
                    case KeyEvent.VK_RIGHT:
                        solidMoveX[selectedSolidIndex] += 0.1;
                        break;
                    case KeyEvent.VK_UP:
                        solidMoveZ[selectedSolidIndex] -= 0.1;
                        break;
                    case KeyEvent.VK_DOWN:
                        solidMoveZ[selectedSolidIndex] += 0.1;
                        break;
                    case KeyEvent.VK_R:
                        solidMoveY[selectedSolidIndex] += 0.1;
                        break;
                    case KeyEvent.VK_F:
                        solidMoveY[selectedSolidIndex] -= 0.1;
                        break;

                    case KeyEvent.VK_C:
                        solidScale[selectedSolidIndex] = Math.max(0.1, solidScale[selectedSolidIndex] - 0.05);
                        break;
                    case KeyEvent.VK_X:
                        solidScale[selectedSolidIndex] += 0.05;
                        break;

                    case KeyEvent.VK_W:
                        moveCamera(CAMERA_MOVE_STEP, 0.0);
                        break;
                    case KeyEvent.VK_S:
                        moveCamera(-CAMERA_MOVE_STEP, 0.0);
                        break;
                    case KeyEvent.VK_A:
                        moveCamera(0.0, -CAMERA_MOVE_STEP);
                        break;
                    case KeyEvent.VK_D:
                        moveCamera(0.0, CAMERA_MOVE_STEP);
                        break;

                    default:
                        redraw = false;
                }

                if (redraw) {
                    updateViewProjectionMatrices();
                    drawScene();
                }
            }
        });

        panel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                // mouse look only while dragging with left button
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                if (leftMouseDown) {
                    updateCameraByMouse(e);
                }
            }
        });

        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                lastMouseX = e.getX();
                lastMouseY = e.getY();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                panel.requestFocusInWindow();
                if (e.getButton() == MouseEvent.BUTTON1) {
                    leftMouseDown = true;
                    lastMouseX = e.getX();
                    lastMouseY = e.getY();
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    leftMouseDown = false;
                    lastMouseX = null;
                    lastMouseY = null;
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                leftMouseDown = false;
                lastMouseX = null;
                lastMouseY = null;
            }
        });


    }

    private void drawScene() {

        zBuffer.clear();

        panel.getRaster().clear();

        rendererSolid.setViewMatrix(viewMatrix);
        rendererSolid.setProjectionMatrix(projectionMatrix);

        for (int i = 0; i < solids.size(); i++) {
            Solid solid = solids.get(i);
            Mat4 model = createModelMatrix(solid, i);
            rendererSolid.render(solid, model);
        }

        panel.repaint();
    }

    private void updateViewProjectionMatrices() {
        int width = panel.getRaster().getWidth();
        int height = panel.getRaster().getHeight();

        projectionMatrix = new Mat4PerspRH(Math.toRadians(60.0), (double) height / width, 0.1, 50.0);

        double cosPitch = Math.cos(cameraPitch);
        Vec3D viewVector = new Vec3D(
                Math.sin(cameraYaw) * cosPitch,
                Math.sin(cameraPitch),
                -Math.cos(cameraYaw) * cosPitch
        );
        viewMatrix = new Mat4ViewRH(cameraPosition, viewVector, new Vec3D(0, 1, 0));
    }

    private void moveCamera(double forwardDelta, double rightDelta) {
        Vec3D forward = new Vec3D(Math.sin(cameraYaw), 0.0, -Math.cos(cameraYaw));
        Vec3D right = new Vec3D(Math.cos(cameraYaw), 0.0, Math.sin(cameraYaw));
        cameraPosition = cameraPosition
                .add(forward.mul(forwardDelta))
                .add(right.mul(rightDelta));
    }

    private void updateCameraByMouse(MouseEvent e) {
        if (lastMouseX == null || lastMouseY == null) {
            lastMouseX = e.getX();
            lastMouseY = e.getY();
            return;
        }

        int dx = e.getX() - lastMouseX;
        int dy = e.getY() - lastMouseY;
        lastMouseX = e.getX();
        lastMouseY = e.getY();

        if (dx == 0 && dy == 0) {
            return;
        }

        cameraYaw += dx * MOUSE_SENSITIVITY;
        cameraPitch -= dy * MOUSE_SENSITIVITY;
        cameraPitch = Math.max(-PITCH_LIMIT, Math.min(PITCH_LIMIT, cameraPitch));

        updateViewProjectionMatrices();
        drawScene();
    }

    private Mat4 createModelMatrix(Solid solid, int solidIndex) {
        double width = panel.getRaster().getWidth();
        double height = panel.getRaster().getHeight();

        Mat4 normalizeToWorld = new Mat4Transl(-width / 2.0, -height / 2.0, 0.0)
                .mul(new Mat4Scale(1.0 / 200.0, -1.0 / 200.0, 1.0));

        Vec3D center = computeCenterInWorld(solid, normalizeToWorld);

        Mat4 selectedTransform = new Mat4Transl(-center.getX(), -center.getY(), -center.getZ())
                .mul(new Mat4Scale(solidScale[solidIndex]))
                .mul(new Mat4RotY(solidRotY[solidIndex]))
                .mul(new Mat4Transl(
                        center.getX() + solidMoveX[solidIndex],
                        center.getY() + solidMoveY[solidIndex],
                        center.getZ() + solidMoveZ[solidIndex]
                ));

        return normalizeToWorld.mul(selectedTransform);
    }

    private Vec3D computeCenterInWorld(Solid solid, Mat4 normalizeToWorld) {
        double sumX = 0.0;
        double sumY = 0.0;
        double sumZ = 0.0;
        int count = 0;

        for (Vertex vertex : solid.getVertexBuffer()) {
            Optional<Vec3D> point = vertex.getPosition().mul(normalizeToWorld).dehomog();
            if (point.isPresent()) {
                Vec3D p = point.get();
                sumX += p.getX();
                sumY += p.getY();
                sumZ += p.getZ();
                count++;
            }
        }

        if (count == 0) {
            return new Vec3D(0, 0, 0);
        }

        return new Vec3D(sumX / count, sumY / count, sumZ / count);
    }
}
