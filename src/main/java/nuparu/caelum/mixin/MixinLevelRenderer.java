package nuparu.caelum.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.*;
import net.minecraft.util.Mth;
import net.minecraft.world.level.material.FogType;
import nuparu.caelum.client.MoonController;
import nuparu.caelum.client.SkyUtils;
import nuparu.caelum.client.data.StarDataManager;
import nuparu.caelum.config.ClientConfig;
import nuparu.caelum.config.LatitudeEffects;
import nuparu.caelum.config.StarsType;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import javax.annotation.Nullable;

@Mixin(LevelRenderer.class)
public class MixinLevelRenderer {
    @Shadow @Nullable private ClientLevel level;

    @Shadow @Nullable private VertexBuffer starBuffer;

    @Shadow private double prevCamZ;

    @Inject(at = @At("HEAD"), method = "renderSky(Lorg/joml/Matrix4f;Lorg/joml/Matrix4f;FLnet/minecraft/client/Camera;ZLjava/lang/Runnable;)V", cancellable = true)
    private void renderSky(Matrix4f p_202424_, Matrix4f p_254034_, float p_202426_, Camera p_202427_, boolean p_202428_, Runnable p_202429_, CallbackInfo ci) {
        SkyUtils.calculateStarLatitudeRotation(level, p_202427_.getPosition().z());
        if(StarDataManager.vanillaStarBuffer == null){
            StarDataManager.vanillaStarBuffer = starBuffer;
        }
        if(ClientConfig.starsType.get() == StarsType.CUSTOM){
            starBuffer = StarDataManager.INSTANCE.getStarBuffer();
        }
        else if(ClientConfig.starsType.get() == StarsType.VANILLA){
            starBuffer = StarDataManager.vanillaStarBuffer;
        }
    }

    @Inject(at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;setShaderColor(FFFF)V", ordinal = 2), method = "renderSky(Lorg/joml/Matrix4f;Lorg/joml/Matrix4f;FLnet/minecraft/client/Camera;ZLjava/lang/Runnable;)V", cancellable = true, locals = LocalCapture.CAPTURE_FAILHARD)
    private void renderSky$customStars(Matrix4f p_202424_, Matrix4f p_254034_, float p_202426_, Camera p_202427_, boolean p_202428_, Runnable p_202429_, CallbackInfo ci, FogType fogType, PoseStack poseStack) {
        if(ClientConfig.starsType.get() != StarsType.CUSTOM) return;
        float f11 = 1.0F - this.level.getRainLevel(p_202426_);
        float f10 = (float) (level.getStarBrightness(p_202426_) * f11 * ClientConfig.starBrightness.get());
        if (f10 > 0.0F) {
            poseStack.pushPose();
            poseStack.mulPose(Axis.XP.rotationDegrees(180.0F));
            poseStack.mulPose(Axis.ZP.rotationDegrees(90.0F));
            if(ClientConfig.latitudeEffects.get() == LatitudeEffects.STARS_ONLY) {
                poseStack.mulPose(Axis.YP.rotationDegrees((float) (180 * SkyUtils.starLatitudeRotation(level, p_202427_.getPosition().z()))));
            }
            poseStack.mulPose(Axis.ZP.rotationDegrees(-level.getTimeOfDay(p_202426_) * 360.0F));
            poseStack.mulPose(Axis.ZP.rotationDegrees((float) (-SkyUtils.yearRotation(level) * 360.0F)));
            RenderSystem.setShaderColor(f10, f10, f10, f10);
            FogRenderer.setupNoFog();
            starBuffer.bind();
            starBuffer.drawWithShader(poseStack.last().pose(), p_254034_, GameRenderer.getPositionColorShader());
            VertexBuffer.unbind();
            p_202429_.run();
            poseStack.popPose();
        }
    }

    @Redirect(at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/VertexBuffer;drawWithShader(Lorg/joml/Matrix4f;Lorg/joml/Matrix4f;Lnet/minecraft/client/renderer/ShaderInstance;)V"), method = "renderSky(Lorg/joml/Matrix4f;Lorg/joml/Matrix4f;FLnet/minecraft/client/Camera;ZLjava/lang/Runnable;)V")
    private void renderSky$skipVanillaStars(VertexBuffer buffer, Matrix4f p_254480_, Matrix4f p_254555_, ShaderInstance p_253993_) {
        if(!buffer.equals(starBuffer) || ClientConfig.starsType.get() == StarsType.VANILLA){
            buffer.drawWithShader(p_254480_, p_254555_, p_253993_);
        }
    }

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/DimensionSpecialEffects;getSunriseColor(FF)[F"), method = "renderSky(Lorg/joml/Matrix4f;Lorg/joml/Matrix4f;FLnet/minecraft/client/Camera;ZLjava/lang/Runnable;)V")
    private float[] renderSky$getSunriseColor(DimensionSpecialEffects effects, float p_108872_, float p_108873_) {
        return SkyUtils.getSunriseColor(level, prevCamZ, p_108873_);
    }
    @Redirect(at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;mulPose(Lorg/joml/Quaternionf;)V", ordinal = 1), method = "renderSky(Lorg/joml/Matrix4f;Lorg/joml/Matrix4f;FLnet/minecraft/client/Camera;ZLjava/lang/Runnable;)V")
    private void renderSky$sunriseRotationRemoveVanilla(PoseStack instance, Quaternionf p_254385_) {

    }
    @Inject(at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;mulPose(Lorg/joml/Quaternionf;)V", ordinal = 1), method = "renderSky(Lorg/joml/Matrix4f;Lorg/joml/Matrix4f;FLnet/minecraft/client/Camera;ZLjava/lang/Runnable;)V", locals = LocalCapture.CAPTURE_FAILHARD)
    private void renderSky$sunriseRotation(Matrix4f p_202424_, Matrix4f p_254034_, float p_202426_, Camera p_202427_, boolean p_202428_, Runnable p_202429_, CallbackInfo ci, FogType fogType, PoseStack poseStack) {
        poseStack.mulPose(Axis.ZP.rotationDegrees((float) SkyUtils.getSunriseColorRotation(level, prevCamZ, p_202426_)));
    }
    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/ClientLevel;getMoonPhase()I"), method = "renderSky(Lorg/joml/Matrix4f;Lorg/joml/Matrix4f;FLnet/minecraft/client/Camera;ZLjava/lang/Runnable;)V", cancellable = true, locals = LocalCapture.CAPTURE_FAILHARD)
    private void renderSky$renderMoon$Pre(Matrix4f p_202424_, Matrix4f p_254034_, float p_202426_, Camera p_202427_, boolean p_202428_, Runnable p_202429_, CallbackInfo ci, FogType fogType, PoseStack poseStack) {
        poseStack.mulPose(Axis.XP.rotation(-MoonController.MOON.getMoonOrbitPosition(level.getDayTime()) * Mth.TWO_PI));
        poseStack.mulPose(Axis.YP.rotationDegrees(90));
        int phase = this.level.getMoonPhase();
        if(phase == 4){
            RenderSystem.setShaderColor(0,0,0,0);
        }
    }

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/ClientLevel;getStarBrightness(F)F"), method = "renderSky(Lorg/joml/Matrix4f;Lorg/joml/Matrix4f;FLnet/minecraft/client/Camera;ZLjava/lang/Runnable;)V", cancellable = true, locals = LocalCapture.CAPTURE_FAILHARD)
    private void renderSky$renderMoon$Post(Matrix4f p_202424_, Matrix4f p_254034_, float p_202426_, Camera p_202427_, boolean p_202428_, Runnable p_202429_, CallbackInfo ci, FogType fogType, PoseStack poseStack) {
        poseStack.mulPose(Axis.XP.rotation(MoonController.MOON.getMoonOrbitPosition(level.getDayTime()) * Mth.TWO_PI));
        poseStack.mulPose(Axis.YP.rotationDegrees(-90));
    }

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/ClientLevel;getRainLevel(F)F"), method = "renderSky(Lorg/joml/Matrix4f;Lorg/joml/Matrix4f;FLnet/minecraft/client/Camera;ZLjava/lang/Runnable;)V", cancellable = true, locals = LocalCapture.CAPTURE_FAILHARD)
    private void renderSky$renderCelestial$Pre(Matrix4f p_202424_, Matrix4f p_254034_, float p_202426_, Camera p_202427_, boolean p_202428_, Runnable p_202429_, CallbackInfo ci, FogType fogType, PoseStack poseStack) {
        if(ClientConfig.latitudeEffects.get() == LatitudeEffects.ALL) {
            poseStack.mulPose(Axis.XP.rotationDegrees((float) (-180 * SkyUtils.starLatitudeRotation(level, p_202427_.getPosition().z()))));
        }
    }
}
