#version 110

uniform sampler2D DiffuseSampler;

varying vec2 texCoord;

uniform float RedClip;
uniform float GreenClip;
uniform float BlueClip;

void main() {
    vec4 InTexel = texture2D(DiffuseSampler, texCoord);

    float Red = InTexel.r;
    float Green = InTexel.g;
    float Blue = InTexel.b;

    float RedOut = Red;
    float GreenOut = Green;
    float BlueOut = Blue;

    if((RedClip > 0.0 && Red < RedClip) || (RedClip < 0.0 && Red > (RedClip * -1.0)) 
    || (GreenClip > 0.0 && Green < RedClip) || (GreenClip < 0.0 && Green > (GreenClip * -1.0)) 
    || (BlueClip > 0.0 && Blue < BlueClip) || (BlueClip < 0.0 && Blue > (BlueClip * -1.0))) {
        RedOut = (Red + Green + Blue) / 3.0;
        GreenOut = RedOut;
        BlueOut = RedOut;
    }

    gl_FragColor = vec4(RedOut, GreenOut, BlueOut, 1.0);
}
