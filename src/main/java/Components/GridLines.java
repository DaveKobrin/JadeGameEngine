package Components;

import Jade.Component;
import Jade.Window;
import Renderer.DebugDrawBatch;
import Utility.Color;
import Utility.Settings;
import org.joml.Vector2f;

public class GridLines extends Component {

    @Override
    public void update(float dt) {
        Vector2f cameraPos = Window.getScene().getCamera().getPosition();
        Vector2f projectionSize = Window.getScene().getCamera().getProjectionSize();
        int width = (int)projectionSize.x;
        int height = (int)projectionSize.y;
        int numLinesV = width / Settings.TILE_WIDTH;
        int numLinesH = height / Settings.TILE_HEIGHT;
        int firstX = ((int)cameraPos.x / Settings.TILE_WIDTH) * Settings.TILE_WIDTH;
        int firstY = ((int)cameraPos.y / Settings.TILE_HEIGHT) * Settings.TILE_HEIGHT;
        Color color = new Color(Color.COLORS.DARK_GRAY);

        for (int i = 0; i < (Math.max(numLinesH,numLinesV)); i++) {
            int x = firstX + (Settings.TILE_WIDTH * i);
            int y = firstY + (Settings.TILE_HEIGHT * i);

            if (i <= numLinesH)
                DebugDrawBatch.addLine2D(new Vector2f(firstX, y), new Vector2f(firstX + width, y), color);

            if (i <= numLinesV)
                DebugDrawBatch.addLine2D(new Vector2f(x, firstY), new Vector2f(x, firstY + height), color);

        }
    }
}
