package Editor;

import imgui.ImGui;
import imgui.flag.ImGuiStyleVar;
import org.joml.Vector2f;

public class JImGui {
    private static final float DEFAULT_COL_WIDTH = 150f;

    public static void vec2fControl(String label, Vector2f value) {
        vec2fControl(label, value, 0f, DEFAULT_COL_WIDTH);
    }

    public static void vec2fControl(String label, Vector2f value, float resetVal) {
        vec2fControl(label, value, resetVal, DEFAULT_COL_WIDTH);
    }

    public static void vec2fControl(String label, Vector2f value, float resetVal, float colWidth) {
        ImGui.pushID(label);
        ImGui.columns(2);
        ImGui.setColumnWidth(0, colWidth);
        ImGui.text(label);
        ImGui.nextColumn();
        ImGui.pushStyleVar(ImGuiStyleVar.ItemSpacing, 0f, 0f);

        float lineHeight = ImGui.getFontSize() + ImGui.getStyle().getFramePaddingY() * 2f;
        Vector2f buttonSize = new Vector2f(lineHeight + 3f, lineHeight);
        float widthEach = (ImGui.calcItemWidth() - buttonSize.x * 2f) / 2f;

        if (ImGui.button("X", buttonSize.x, buttonSize.y)) {
            value.x = resetVal;
        }

        ImGui.sameLine();

        ImGui.pushItemWidth(widthEach);
        float[] valueX = {value.x};
        ImGui.dragFloat("##x", valueX, 0.1f);
        value.x = valueX[0];
        ImGui.popItemWidth();

        ImGui.sameLine();

        if (ImGui.button("Y", buttonSize.x, buttonSize.y)) {
            value.y = resetVal;
        }

        ImGui.sameLine();
        ImGui.pushItemWidth(widthEach);
        float[] valueY = {value.y};
        ImGui.dragFloat("##y", valueY, 0.1f);
        value.y = valueY[0];
        ImGui.popItemWidth();

//        ImGui.nextColumn();

        ImGui.columns(1);
        ImGui.popStyleVar();
        ImGui.popID();
    }
}
