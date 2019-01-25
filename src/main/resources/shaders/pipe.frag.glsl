#version 330 core

layout(location = 0) out vec4 color;


in Data {
    vec2 tc;
    vec3 position;
} fs_in;

uniform sampler2D tex;
uniform int top = 0;
uniform vec2 bird;

void main() {
    float x = fs_in.tc.x;
    float y = fs_in.tc.y;

    if (top == 1) {
        y =  1.0 - y;
    }

    color = texture(tex, vec2(x, y));
    if (color.w < 1.0) {
        discard;
    }

    color *= 2.0 / (length(bird - fs_in.position.xy) + 1.5) + 0.4;
}