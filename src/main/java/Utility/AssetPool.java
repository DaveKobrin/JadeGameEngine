package Utility;

import Components.Spritesheet;
import Renderer.Shader;
import Renderer.Texture;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class AssetPool {
    private static Map<String, Shader> shaders = new HashMap<>();
    private static Map<String, Texture> textures = new HashMap<>();
    private static Map<String, Spritesheet> spritesheets = new HashMap<>();

    public static void addShaderResource(String resourceName) {
        File file = new File(resourceName);
        if (!AssetPool.shaders.containsKey(file.getAbsolutePath())) {
            Shader shader = new Shader(resourceName);
            shader.compile();
            AssetPool.shaders.put(file.getAbsolutePath(), shader);
        }
    }

    public static void addTextureResource(String resourceName) {
        File file = new File(resourceName);
        if (!AssetPool.textures.containsKey(file.getAbsolutePath())) {
            Texture texture = new Texture(resourceName);
            AssetPool.textures.put(file.getAbsolutePath(), texture);
        }
    }

    public static void addSpritesheetResource(String resourceName, Spritesheet spritesheet) {
        File file = new File(resourceName);
        if (!AssetPool.spritesheets.containsKey(file.getAbsolutePath())) {
            AssetPool.spritesheets.put(file.getAbsolutePath(), spritesheet);
        }
    }

    public static Shader getShader(String resourceName) {
        File file = new File(resourceName);
        if (AssetPool.shaders.containsKey(file.getAbsolutePath())) {
            return AssetPool.shaders.get(file.getAbsolutePath());
        } else {
            assert false : "Resource not found in asset pool'" + resourceName + "'";
            return null;
        }
    }

    public static Texture getTexture(String resourceName) {
        File file = new File(resourceName);
        if (AssetPool.textures.containsKey(file.getAbsolutePath())) {
            return AssetPool.textures.get(file.getAbsolutePath());
        } else {
            assert false : "Resource not found in asset pool'" + resourceName + "'";
            return null;
        }
    }
    public static Spritesheet getSpritesheet(String resourceName) {
        File file = new File(resourceName);
        if (AssetPool.spritesheets.containsKey(file.getAbsolutePath())) {
            return AssetPool.spritesheets.get(file.getAbsolutePath());
        } else {
            assert false : "Resource not found in asset pool'" + resourceName + "'";
            return null;
        }
    }
}
