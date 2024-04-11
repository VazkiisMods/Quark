#version 110

uniform sampler2D DiffuseSampler;

varying vec2 texCoord;

uniform float Clip;

void main() {
    vec4 InTexel = texture2D(DiffuseSampler, texCoord);
    float Gray = (InTexel.r + InTexel.g + InTexel.g) / 3.0;

    if(Gray > Clip)
        gl_FragColor = vec4(1.0, 1.0, 1.0, 1.0);
    else gl_FragColor = vec4(0.0, 0.0, 0.0, 1.0);
}
