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
import java.util.Arrays;
import java.util.List;
import java.util.Optional;


public class Controller3D {
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
    private double cameraYaw = 0.0;
    private double cameraDistance = 4.0;

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

                    case KeyEvent.VK_A:
                        cameraYaw -= 0.08;
                        break;
                    case KeyEvent.VK_D:
                        cameraYaw += 0.08;
                        break;
                    case KeyEvent.VK_W:
                        cameraDistance = Math.max(1.0, cameraDistance - 0.15);
                        break;
                    case KeyEvent.VK_S:
                        cameraDistance = Math.min(20.0, cameraDistance + 0.15);
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

        Vec3D eye = new Vec3D(
                Math.sin(cameraYaw) * cameraDistance,
                1.5,
                Math.cos(cameraYaw) * cameraDistance
        );
        Vec3D viewVector = new Vec3D(-eye.getX(), -eye.getY(), -eye.getZ());
        viewMatrix = new Mat4ViewRH(eye, viewVector, new Vec3D(0, 1, 0));
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
