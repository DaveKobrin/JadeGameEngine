#type vertex
#version 330 core
layout (location=0) in vec3 aPos;
layout (location=1) in vec4 aColor;
layout (location=2) in vec2 aTexCoords;
layout (location=3) in float aTexID;

uniform mat4 uProjection;
uniform mat4 uView;

out vec4 fColor;
out vec2 fTexCoords;
out float fTexID;

void main()
{
    fColor = aColor;
    fTexCoords = aTexCoords;
    fTexID = aTexID;
    gl_Position = uProjection * uView * vec4(aPos, 1.0);
}

#type fragment
#version 330 core

//uniform float uTime;
uniform sampler2D uTextures[16];

in vec4 fColor;
in vec2 fTexCoords;
in float fTexID;

out vec4 color;

void main()
{
//    float noise = fract(sin(dot(fColor.xy, vec2(12.9898, 78.233))) * 43758.5453);
//    float avg = (fColor.r + fColor.g + fColor.b)/3;

    if (fTexID > 0) {
        int texID = int(fTexID);
        color = texture(uTextures[texID], fTexCoords);
    } else {
        color = fColor;
    }
}