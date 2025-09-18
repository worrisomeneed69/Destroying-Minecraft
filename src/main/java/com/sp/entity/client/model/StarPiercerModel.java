package com.sp.entity.client.model;

import com.sp.DestroyingMinecraft;
import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;

public class StarPiercerModel extends EntityModel<Entity> {
	public static final EntityModelLayer STAR_PIERCER_MODEL_LAYER = new EntityModelLayer(DestroyingMinecraft.idOf("starpiercer"), "main");
	public final ModelPart starpiercer;
	public final ModelPart body;
	public final ModelPart barrel;
	public final ModelPart legs;

	public StarPiercerModel(ModelPart root) {
		this.starpiercer = root.getChild("starpiercer");
		this.body = this.starpiercer.getChild("body");
		this.barrel = this.body.getChild("barrel");
		this.legs = this.starpiercer.getChild("legs");
	}

	public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData starpiercer = modelPartData.addChild("starpiercer", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 24.0F, 0.0F));

        ModelPartData body = starpiercer.addChild("body", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, -83.0F, 0.0F));

        ModelPartData front = body.addChild("front", ModelPartBuilder.create(), ModelTransform.pivot(-22.0F, 0.0F, -193.0F));

        ModelPartData left = front.addChild("left", ModelPartBuilder.create().uv(481, 520).cuboid(10.0F, -22.5F, -117.0F, 6.0F, 24.0F, 234.0F, new Dilation(0.0F))
                .uv(91, 1103).cuboid(-28.9F, -25.5F, -142.0F, 22.0F, 30.0F, 20.0F, new Dilation(0.0F)), ModelTransform.pivot(61.9F, -4.5F, 18.0F));

        ModelPartData cube_r1 = left.addChild("cube_r1", ModelPartBuilder.create().uv(0, 1038).cuboid(-30.0F, -15.0F, 0.0F, 30.0F, 30.0F, 34.0F, new Dilation(0.01F)), ModelTransform.of(-6.9F, -10.5F, -142.0F, 0.0F, 0.7418F, 0.0F));

        ModelPartData cube_r2 = left.addChild("cube_r2", ModelPartBuilder.create().uv(0, 260).cuboid(-1.5F, -12.0F, -117.0F, 6.0F, 25.0F, 234.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.5708F));

        ModelPartData cube_r3 = left.addChild("cube_r3", ModelPartBuilder.create().uv(0, 0).cuboid(-1.5F, -12.0F, -117.0F, 6.0F, 25.0F, 234.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -21.0F, 0.0F, 0.0F, 0.0F, -1.5708F));

        ModelPartData cube_r4 = left.addChild("cube_r4", ModelPartBuilder.create().uv(0, 520).cuboid(-1.5F, -12.0F, -117.0F, 6.0F, 24.0F, 234.0F, new Dilation(0.0F)), ModelTransform.of(-11.5F, -10.5F, 0.0F, 0.0F, 0.0F, -3.1416F));

        ModelPartData ribs = left.addChild("ribs", ModelPartBuilder.create().uv(258, 1038).cuboid(-14.9F, -28.5F, 93.0F, 34.0F, 36.0F, 24.0F, new Dilation(0.0F))
                .uv(375, 1038).cuboid(-14.9F, -28.5F, 48.0F, 34.0F, 36.0F, 24.0F, new Dilation(0.0F))
                .uv(492, 1038).cuboid(-14.9F, -28.5F, 3.0F, 34.0F, 36.0F, 24.0F, new Dilation(0.0F))
                .uv(609, 1038).cuboid(-14.9F, -28.5F, -42.0F, 34.0F, 36.0F, 24.0F, new Dilation(0.0F))
                .uv(726, 1038).cuboid(-14.9F, -28.5F, -87.0F, 34.0F, 36.0F, 24.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData set8 = left.addChild("set8", ModelPartBuilder.create().uv(586, 1147).cuboid(59.0F, -19.0F, -74.0F, 10.0F, 8.0F, 8.0F, new Dilation(0.0F))
                .uv(1208, 753).cuboid(59.0F, -19.0F, -119.0F, 10.0F, 8.0F, 8.0F, new Dilation(0.0F))
                .uv(655, 1099).cuboid(61.0F, -19.0F, -111.0F, 8.0F, 8.0F, 37.0F, new Dilation(0.0F)), ModelTransform.pivot(-39.9F, 4.5F, 175.0F));

        ModelPartData set9 = left.addChild("set9", ModelPartBuilder.create().uv(980, 1208).cuboid(59.0F, -19.0F, -74.0F, 10.0F, 8.0F, 8.0F, new Dilation(0.0F))
                .uv(1017, 1208).cuboid(59.0F, -19.0F, -119.0F, 10.0F, 8.0F, 8.0F, new Dilation(0.0F))
                .uv(746, 1099).cuboid(61.0F, -19.0F, -111.0F, 8.0F, 8.0F, 37.0F, new Dilation(0.0F)), ModelTransform.pivot(-39.9F, 4.5F, 40.0F));

        ModelPartData right = front.addChild("right", ModelPartBuilder.create().uv(481, 779).cuboid(-16.0F, -22.5F, -117.0F, 6.0F, 24.0F, 234.0F, new Dilation(0.0F))
                .uv(375, 1104).cuboid(6.9F, -25.5F, -142.0F, 22.0F, 30.0F, 20.0F, new Dilation(0.0F)), ModelTransform.pivot(-17.9F, -4.5F, 18.0F));

        ModelPartData cube_r5 = right.addChild("cube_r5", ModelPartBuilder.create().uv(129, 1038).cuboid(0.0F, -15.0F, 0.0F, 30.0F, 30.0F, 34.0F, new Dilation(0.01F)), ModelTransform.of(6.9F, -10.5F, -142.0F, 0.0F, -0.7418F, 0.0F));

        ModelPartData cube_r6 = right.addChild("cube_r6", ModelPartBuilder.create().uv(481, 260).cuboid(-4.5F, -12.0F, -117.0F, 6.0F, 25.0F, 234.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -1.5708F));

        ModelPartData cube_r7 = right.addChild("cube_r7", ModelPartBuilder.create().uv(481, 0).cuboid(-4.5F, -12.0F, -117.0F, 6.0F, 25.0F, 234.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -21.0F, 0.0F, 0.0F, 0.0F, 1.5708F));

        ModelPartData cube_r8 = right.addChild("cube_r8", ModelPartBuilder.create().uv(0, 779).cuboid(-4.5F, -12.0F, -117.0F, 6.0F, 24.0F, 234.0F, new Dilation(0.0F)), ModelTransform.of(11.5F, -10.5F, 0.0F, 0.0F, 0.0F, 3.1416F));

        ModelPartData ribs2 = right.addChild("ribs2", ModelPartBuilder.create().uv(843, 1038).cuboid(-19.1F, -28.5F, 93.0F, 34.0F, 36.0F, 24.0F, new Dilation(0.0F))
                .uv(1087, 1026).cuboid(-19.1F, -28.5F, 48.0F, 34.0F, 36.0F, 24.0F, new Dilation(0.0F))
                .uv(1087, 1087).cuboid(-19.1F, -28.5F, 3.0F, 34.0F, 36.0F, 24.0F, new Dilation(0.0F))
                .uv(1091, 553).cuboid(-19.1F, -28.5F, -42.0F, 34.0F, 36.0F, 24.0F, new Dilation(0.0F))
                .uv(1091, 614).cuboid(-19.1F, -28.5F, -87.0F, 34.0F, 36.0F, 24.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData set10 = right.addChild("set10", ModelPartBuilder.create().uv(1054, 1208).cuboid(-69.0F, -19.0F, -74.0F, 10.0F, 8.0F, 8.0F, new Dilation(0.0F))
                .uv(1091, 1208).cuboid(-69.0F, -19.0F, -119.0F, 10.0F, 8.0F, 8.0F, new Dilation(0.0F))
                .uv(837, 1099).cuboid(-69.0F, -19.0F, -111.0F, 8.0F, 8.0F, 37.0F, new Dilation(0.0F)), ModelTransform.pivot(39.9F, 4.5F, 175.0F));

        ModelPartData set11 = right.addChild("set11", ModelPartBuilder.create().uv(1128, 1208).cuboid(-69.0F, -19.0F, -74.0F, 10.0F, 8.0F, 8.0F, new Dilation(0.0F))
                .uv(0, 1209).cuboid(-69.0F, -19.0F, -119.0F, 10.0F, 8.0F, 8.0F, new Dilation(0.0F))
                .uv(0, 1103).cuboid(-69.0F, -19.0F, -111.0F, 8.0F, 8.0F, 37.0F, new Dilation(0.0F)), ModelTransform.pivot(39.9F, 4.5F, 40.0F));

        ModelPartData barrelsupport = body.addChild("barrelsupport", ModelPartBuilder.create().uv(962, 405).cuboid(-23.0F, -40.0F, -58.0F, 46.0F, 10.0F, 63.0F, new Dilation(0.0F))
                .uv(962, 479).cuboid(-23.0F, 0.0F, -58.0F, 46.0F, 10.0F, 63.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData cube_r9 = barrelsupport.addChild("cube_r9", ModelPartBuilder.create().uv(962, 913).cuboid(-23.0F, -10.0F, 0.0F, 46.0F, 10.0F, 47.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 10.0F, 5.0F, 0.2182F, 0.0F, 0.0F));

        ModelPartData cube_r10 = barrelsupport.addChild("cube_r10", ModelPartBuilder.create().uv(962, 855).cuboid(-23.0F, 0.0F, 0.0F, 46.0F, 10.0F, 47.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -40.0F, 5.0F, -0.2182F, 0.0F, 0.0F));

        ModelPartData details = body.addChild("details", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData pipes = details.addChild("pipes", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData set1 = pipes.addChild("set1", ModelPartBuilder.create().uv(585, 1104).cuboid(23.0F, -32.0F, -38.0F, 17.0F, 2.0F, 2.0F, new Dilation(1.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData cube_r11 = set1.addChild("cube_r11", ModelPartBuilder.create().uv(165, 1154).cuboid(-1.0F, 1.0F, 1.0F, 2.0F, 2.0F, 3.0F, new Dilation(1.0F)), ModelTransform.of(43.0F, -33.0F, 36.0F, -0.7854F, 0.0F, 0.0F));

        ModelPartData cube_r12 = set1.addChild("cube_r12", ModelPartBuilder.create().uv(1091, 841).cuboid(-1.0F, -2.0F, -1.0F, 73.0F, 2.0F, 2.0F, new Dilation(1.0F)), ModelTransform.of(43.0F, -30.0F, -37.0F, 0.0F, -1.5708F, 0.0F));

        ModelPartData set2 = pipes.addChild("set2", ModelPartBuilder.create().uv(586, 1164).cuboid(23.0F, -32.0F, -34.0F, 17.0F, 2.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData cube_r13 = set2.addChild("cube_r13", ModelPartBuilder.create().uv(985, 1155).cuboid(-1.0F, 1.0F, 1.0F, 2.0F, 2.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(39.0F, -33.4F, 36.0F, -0.7854F, 0.0F, 0.0F));

        ModelPartData cube_r14 = set2.addChild("cube_r14", ModelPartBuilder.create().uv(1091, 846).cuboid(-1.0F, -2.0F, -1.0F, 70.0F, 2.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(39.0F, -30.0F, -33.0F, 0.0F, -1.5708F, 0.0F));

        ModelPartData set3 = pipes.addChild("set3", ModelPartBuilder.create().uv(928, 1099).cuboid(23.0F, -32.0F, -31.0F, 14.0F, 2.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData cube_r15 = set3.addChild("cube_r15", ModelPartBuilder.create().uv(165, 1160).cuboid(-1.0F, 1.0F, 1.0F, 2.0F, 2.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(36.0F, -33.4F, 38.0F, -0.7854F, 0.0F, 0.0F));

        ModelPartData cube_r16 = set3.addChild("cube_r16", ModelPartBuilder.create().uv(375, 1099).cuboid(-1.0F, -2.0F, -1.0F, 69.0F, 2.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(36.0F, -30.0F, -30.0F, 0.0F, -1.5708F, 0.0F));

        ModelPartData set4 = pipes.addChild("set4", ModelPartBuilder.create().uv(928, 1104).cuboid(20.0F, -32.0F, -28.0F, 14.0F, 2.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData cube_r17 = set4.addChild("cube_r17", ModelPartBuilder.create().uv(518, 1099).cuboid(1.0F, -2.0F, -1.0F, 66.0F, 2.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(33.0F, -30.0F, -27.0F, 0.0F, -1.5708F, 0.0F));

        ModelPartData cube_r18 = set4.addChild("cube_r18", ModelPartBuilder.create().uv(985, 1161).cuboid(-1.0F, 1.0F, 1.0F, 2.0F, 2.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(33.0F, -33.4F, 40.0F, -0.7854F, 0.0F, 0.0F));

        ModelPartData set5 = pipes.addChild("set5", ModelPartBuilder.create().uv(61, 1149).cuboid(20.0F, -32.0F, -23.0F, 9.0F, 2.0F, 2.0F, new Dilation(2.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData cube_r19 = set5.addChild("cube_r19", ModelPartBuilder.create().uv(460, 1104).cuboid(3.0F, -2.0F, -1.0F, 60.0F, 2.0F, 2.0F, new Dilation(2.0F)), ModelTransform.of(28.0F, -30.0F, -20.0F, 0.0F, -1.5708F, 0.0F));

        ModelPartData cube_r20 = set5.addChild("cube_r20", ModelPartBuilder.create().uv(165, 1166).cuboid(-1.0F, 1.0F, 1.0F, 2.0F, 2.0F, 3.0F, new Dilation(2.0F)), ModelTransform.of(28.0F, -32.6F, 45.0F, -0.7854F, 0.0F, 0.0F));

        ModelPartData set6 = pipes.addChild("set6", ModelPartBuilder.create().uv(305, 1176).cuboid(-11.0F, -49.0F, -39.0F, 6.0F, 6.0F, 23.0F, new Dilation(1.0F))
                .uv(1208, 770).cuboid(-11.0F, -49.0F, -47.0F, 6.0F, 8.0F, 6.0F, new Dilation(1.0F))
                .uv(1210, 867).cuboid(-11.0F, -49.0F, -14.0F, 6.0F, 8.0F, 6.0F, new Dilation(1.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData set7 = pipes.addChild("set7", ModelPartBuilder.create().uv(1168, 1177).cuboid(5.0F, -49.0F, -39.0F, 6.0F, 6.0F, 23.0F, new Dilation(1.0F))
                .uv(1210, 882).cuboid(5.0F, -49.0F, -47.0F, 6.0F, 8.0F, 6.0F, new Dilation(1.0F))
                .uv(1210, 897).cuboid(5.0F, -49.0F, -14.0F, 6.0F, 8.0F, 6.0F, new Dilation(1.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData set12 = pipes.addChild("set12", ModelPartBuilder.create().uv(586, 1169).cuboid(-3.0F, -1.2559F, 7.4512F, 14.0F, 2.0F, 2.0F, new Dilation(0.0F))
                .uv(666, 1183).cuboid(-3.0F, -1.2559F, -8.5488F, 2.0F, 2.0F, 18.0F, new Dilation(0.0F)), ModelTransform.pivot(8.0F, -30.7441F, 65.5488F));

        ModelPartData cube_r21 = set12.addChild("cube_r21", ModelPartBuilder.create().uv(985, 1167).cuboid(-1.0F, 0.0F, -3.0F, 2.0F, 2.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(-2.0F, -1.2559F, -8.5488F, 0.7854F, 0.0F, 0.0F));

        ModelPartData set13 = pipes.addChild("set13", ModelPartBuilder.create().uv(1210, 912).cuboid(-11.0F, -1.2559F, 7.4512F, 14.0F, 2.0F, 2.0F, new Dilation(0.0F))
                .uv(1202, 944).cuboid(1.0F, -1.2559F, -8.5488F, 2.0F, 2.0F, 18.0F, new Dilation(0.0F)), ModelTransform.pivot(-8.0F, -30.7441F, 65.5488F));

        ModelPartData cube_r22 = set13.addChild("cube_r22", ModelPartBuilder.create().uv(609, 1174).cuboid(-1.0F, 0.0F, -3.0F, 2.0F, 2.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(2.0F, -1.2559F, -8.5488F, 0.7854F, 0.0F, 0.0F));

        ModelPartData set14 = pipes.addChild("set14", ModelPartBuilder.create().uv(122, 1154).cuboid(-44.0F, -37.0F, 23.0F, 6.0F, 6.0F, 15.0F, new Dilation(1.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData cube_r23 = set14.addChild("cube_r23", ModelPartBuilder.create().uv(426, 1155).cuboid(-3.0F, 1.0F, 1.0F, 6.0F, 6.0F, 10.0F, new Dilation(1.0F)), ModelTransform.of(-41.0F, -38.0F, 39.0F, -0.7854F, 0.0F, 0.0F));

        ModelPartData sights = details.addChild("sights", ModelPartBuilder.create().uv(962, 1026).cuboid(-50.0F, -134.0F, -22.0F, 18.0F, 40.0F, 44.0F, new Dilation(0.0F))
                .uv(1210, 851).cuboid(-48.0F, -132.0F, -24.0F, 14.0F, 13.0F, 2.0F, new Dilation(0.0F))
                .uv(258, 1099).cuboid(-48.0F, -137.0F, -22.0F, 14.0F, 3.0F, 44.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 83.0F, 0.0F));

        ModelPartData exhaust = details.addChild("exhaust", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData left_exhaust = exhaust.addChild("left_exhaust", ModelPartBuilder.create().uv(1005, 1111).cuboid(19.0F, -42.0F, 68.0F, 16.0F, 12.0F, 24.0F, new Dilation(0.0F))
                .uv(1149, 944).cuboid(19.0F, -55.0F, 82.0F, 16.0F, 13.0F, 10.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData cube_r24 = left_exhaust.addChild("cube_r24", ModelPartBuilder.create().uv(790, 1145).cuboid(-7.0F, 0.0F, -19.0F, 14.0F, 13.0F, 19.0F, new Dilation(0.0F)), ModelTransform.of(27.0F, -55.0F, 82.0F, 0.7418F, 0.0F, 0.0F));

        ModelPartData right_exhaust = exhaust.addChild("right_exhaust", ModelPartBuilder.create().uv(709, 1145).cuboid(-35.0F, -42.0F, 68.0F, 16.0F, 12.0F, 24.0F, new Dilation(0.0F))
                .uv(1202, 814).cuboid(-35.0F, -55.0F, 82.0F, 16.0F, 13.0F, 10.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData cube_r25 = right_exhaust.addChild("cube_r25", ModelPartBuilder.create().uv(857, 1145).cuboid(-7.0F, 0.0F, -19.0F, 14.0F, 13.0F, 19.0F, new Dilation(0.0F)), ModelTransform.of(-27.0F, -55.0F, 82.0F, 0.7418F, 0.0F, 0.0F));

        ModelPartData vents = details.addChild("vents", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData cube_r26 = vents.addChild("cube_r26", ModelPartBuilder.create().uv(1204, 1125).cuboid(-12.0F, 0.0F, -1.0F, 24.0F, 2.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 3.0F, 33.4F, 0.2182F, 0.0F, 0.0F));

        ModelPartData cube_r27 = vents.addChild("cube_r27", ModelPartBuilder.create().uv(1204, 1130).cuboid(-12.0F, 0.0F, -1.0F, 24.0F, 2.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 4.0F, 28.4F, 0.2182F, 0.0F, 0.0F));

        ModelPartData cube_r28 = vents.addChild("cube_r28", ModelPartBuilder.create().uv(1204, 1135).cuboid(-12.0F, 0.0F, -1.0F, 24.0F, 2.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 5.0F, 23.4F, 0.2182F, 0.0F, 0.0F));

        ModelPartData cube_r29 = vents.addChild("cube_r29", ModelPartBuilder.create().uv(1204, 1140).cuboid(-12.0F, 0.0F, -1.0F, 24.0F, 2.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 6.0F, 18.4F, 0.2182F, 0.0F, 0.0F));

        ModelPartData cube_r30 = vents.addChild("cube_r30", ModelPartBuilder.create().uv(1202, 965).cuboid(-12.0F, -2.0F, -1.0F, 24.0F, 2.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -33.0F, 33.4F, -0.2182F, 0.0F, 0.0F));

        ModelPartData cube_r31 = vents.addChild("cube_r31", ModelPartBuilder.create().uv(1139, 1019).cuboid(-12.0F, -2.0F, -1.0F, 24.0F, 2.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -34.0F, 28.4F, -0.2182F, 0.0F, 0.0F));

        ModelPartData cube_r32 = vents.addChild("cube_r32", ModelPartBuilder.create().uv(1139, 1014).cuboid(-12.0F, -2.0F, -1.0F, 24.0F, 2.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -35.0F, 23.4F, -0.2182F, 0.0F, 0.0F));

        ModelPartData cube_r33 = vents.addChild("cube_r33", ModelPartBuilder.create().uv(1139, 1009).cuboid(-12.0F, -2.0F, -1.0F, 24.0F, 2.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -36.0F, 18.4F, -0.2182F, 0.0F, 0.0F));

        ModelPartData ribs3 = details.addChild("ribs3", ModelPartBuilder.create().uv(626, 1104).cuboid(-2.0F, -32.0F, 112.0F, 4.0F, 34.0F, 5.0F, new Dilation(0.0F))
                .uv(666, 1204).cuboid(22.0F, -32.0F, 112.0F, 4.0F, 34.0F, 5.0F, new Dilation(0.0F))
                .uv(961, 1186).cuboid(10.0F, -32.0F, 112.0F, 4.0F, 34.0F, 5.0F, new Dilation(0.0F))
                .uv(285, 1207).cuboid(-14.0F, -32.0F, 112.0F, 4.0F, 34.0F, 5.0F, new Dilation(0.0F))
                .uv(685, 1204).cuboid(-26.0F, -32.0F, 112.0F, 4.0F, 34.0F, 5.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData barrel = body.addChild("barrel", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, -14.0F, 0.0F));

        ModelPartData muzzle = barrel.addChild("muzzle", ModelPartBuilder.create().uv(1091, 785).cuboid(-20.0F, -20.0F, -7.5F, 40.0F, 40.0F, 15.0F, new Dilation(0.0F))
                .uv(176, 1147).cuboid(-8.0F, -19.0F, -22.5F, 16.0F, 13.0F, 15.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, -269.5F));

        ModelPartData cube_r34 = muzzle.addChild("cube_r34", ModelPartBuilder.create().uv(1131, 1148).cuboid(-8.0F, -19.0F, -9.0F, 16.0F, 13.0F, 15.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, -13.5F, 0.0F, 0.0F, -0.7854F));

        ModelPartData cube_r35 = muzzle.addChild("cube_r35", ModelPartBuilder.create().uv(1068, 1148).cuboid(-8.0F, -19.0F, -9.0F, 16.0F, 13.0F, 15.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, -13.5F, 0.0F, 0.0F, -1.5708F));

        ModelPartData cube_r36 = muzzle.addChild("cube_r36", ModelPartBuilder.create().uv(1005, 1148).cuboid(-8.0F, -19.0F, -9.0F, 16.0F, 13.0F, 15.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, -13.5F, 0.0F, 0.0F, -2.3562F));

        ModelPartData cube_r37 = muzzle.addChild("cube_r37", ModelPartBuilder.create().uv(523, 1147).cuboid(-8.0F, -19.0F, -9.0F, 16.0F, 13.0F, 15.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, -13.5F, 0.0F, 0.0F, -3.1416F));

        ModelPartData cube_r38 = muzzle.addChild("cube_r38", ModelPartBuilder.create().uv(460, 1147).cuboid(-8.0F, -19.0F, -9.0F, 16.0F, 13.0F, 15.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, -13.5F, 0.0F, 0.0F, 2.3562F));

        ModelPartData cube_r39 = muzzle.addChild("cube_r39", ModelPartBuilder.create().uv(302, 1147).cuboid(-8.0F, -19.0F, -9.0F, 16.0F, 13.0F, 15.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, -13.5F, 0.0F, 0.0F, 1.5708F));

        ModelPartData cube_r40 = muzzle.addChild("cube_r40", ModelPartBuilder.create().uv(239, 1147).cuboid(-8.0F, -19.0F, -9.0F, 16.0F, 13.0F, 15.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, -13.5F, 0.0F, 0.0F, 0.7854F));

        ModelPartData actual_barrel = barrel.addChild("actual_barrel", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, -168.0F));

        ModelPartData light3 = actual_barrel.addChild("light3", ModelPartBuilder.create().uv(962, 180).cuboid(-8.0F, -16.0F, -96.0F, 16.0F, 16.0F, 208.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 8.0F, 0.0F));

        ModelPartData bottom_right = actual_barrel.addChild("bottom_right", ModelPartBuilder.create().uv(0, 1149).cuboid(-18.0F, -8.0F, -94.0F, 18.0F, 18.0F, 12.0F, new Dilation(0.0F))
                .uv(790, 1178).cuboid(-16.0F, 0.0F, -82.0F, 8.0F, 8.0F, 20.0F, new Dilation(0.0F))
                .uv(847, 1178).cuboid(-16.0F, 0.0F, -50.0F, 8.0F, 8.0F, 20.0F, new Dilation(0.0F))
                .uv(0, 1180).cuboid(-16.0F, -8.0F, -62.0F, 16.0F, 16.0F, 12.0F, new Dilation(0.0F))
                .uv(1181, 405).cuboid(-16.0F, 0.0F, -18.0F, 8.0F, 8.0F, 20.0F, new Dilation(0.0F))
                .uv(1149, 851).cuboid(-18.0F, -8.0F, -30.0F, 18.0F, 18.0F, 12.0F, new Dilation(0.0F))
                .uv(1181, 434).cuboid(-16.0F, 0.0F, 14.0F, 8.0F, 8.0F, 20.0F, new Dilation(0.0F))
                .uv(1181, 463).cuboid(-16.0F, -8.0F, 2.0F, 16.0F, 16.0F, 12.0F, new Dilation(0.0F))
                .uv(1181, 492).cuboid(-16.0F, 0.0F, 46.0F, 8.0F, 8.0F, 20.0F, new Dilation(0.0F))
                .uv(1149, 882).cuboid(-18.0F, -8.0F, 34.0F, 18.0F, 18.0F, 12.0F, new Dilation(0.0F))
                .uv(1181, 521).cuboid(-16.0F, 0.0F, 78.0F, 8.0F, 8.0F, 20.0F, new Dilation(0.0F))
                .uv(709, 1182).cuboid(-16.0F, -8.0F, 66.0F, 16.0F, 16.0F, 12.0F, new Dilation(0.0F))
                .uv(1149, 913).cuboid(-18.0F, -8.0F, 98.0F, 18.0F, 18.0F, 12.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 8.0F, 0.0F));

        ModelPartData bottom_left = actual_barrel.addChild("bottom_left", ModelPartBuilder.create().uv(61, 1154).cuboid(0.0F, -8.0F, -94.0F, 18.0F, 18.0F, 12.0F, new Dilation(0.0F))
                .uv(609, 1183).cuboid(8.0F, 0.0F, -82.0F, 8.0F, 8.0F, 20.0F, new Dilation(0.0F))
                .uv(57, 1185).cuboid(8.0F, 0.0F, -50.0F, 8.0F, 8.0F, 20.0F, new Dilation(0.0F))
                .uv(364, 1186).cuboid(0.0F, -8.0F, -62.0F, 16.0F, 16.0F, 12.0F, new Dilation(0.0F))
                .uv(904, 1186).cuboid(8.0F, 0.0F, -18.0F, 8.0F, 8.0F, 20.0F, new Dilation(0.0F))
                .uv(365, 1155).cuboid(0.0F, -8.0F, -30.0F, 18.0F, 18.0F, 12.0F, new Dilation(0.0F))
                .uv(1194, 1148).cuboid(8.0F, 0.0F, 14.0F, 8.0F, 8.0F, 20.0F, new Dilation(0.0F))
                .uv(1202, 785).cuboid(0.0F, -8.0F, 2.0F, 16.0F, 16.0F, 12.0F, new Dilation(0.0F))
                .uv(1204, 1009).cuboid(8.0F, 0.0F, 46.0F, 8.0F, 8.0F, 20.0F, new Dilation(0.0F))
                .uv(924, 1155).cuboid(0.0F, -8.0F, 34.0F, 18.0F, 18.0F, 12.0F, new Dilation(0.0F))
                .uv(1204, 1038).cuboid(8.0F, 0.0F, 78.0F, 8.0F, 8.0F, 20.0F, new Dilation(0.0F))
                .uv(1204, 1067).cuboid(0.0F, -8.0F, 66.0F, 16.0F, 16.0F, 12.0F, new Dilation(0.0F))
                .uv(122, 1176).cuboid(0.0F, -8.0F, 98.0F, 18.0F, 18.0F, 12.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 8.0F, 0.0F));

        ModelPartData top_left = actual_barrel.addChild("top_left", ModelPartBuilder.create().uv(183, 1176).cuboid(0.0F, -10.0F, -94.0F, 18.0F, 18.0F, 12.0F, new Dilation(0.0F))
                .uv(1204, 1096).cuboid(8.0F, -8.0F, -82.0F, 8.0F, 8.0F, 20.0F, new Dilation(0.0F))
                .uv(305, 1206).cuboid(8.0F, -8.0F, -50.0F, 8.0F, 8.0F, 20.0F, new Dilation(0.0F))
                .uv(114, 1207).cuboid(0.0F, -8.0F, -62.0F, 16.0F, 16.0F, 12.0F, new Dilation(0.0F))
                .uv(171, 1207).cuboid(8.0F, -8.0F, -18.0F, 8.0F, 8.0F, 20.0F, new Dilation(0.0F))
                .uv(244, 1176).cuboid(0.0F, -10.0F, -30.0F, 18.0F, 18.0F, 12.0F, new Dilation(0.0F))
                .uv(228, 1207).cuboid(8.0F, -8.0F, 14.0F, 8.0F, 8.0F, 20.0F, new Dilation(0.0F))
                .uv(421, 1207).cuboid(0.0F, -8.0F, 2.0F, 16.0F, 16.0F, 12.0F, new Dilation(0.0F))
                .uv(478, 1207).cuboid(8.0F, -8.0F, 46.0F, 8.0F, 8.0F, 20.0F, new Dilation(0.0F))
                .uv(426, 1176).cuboid(0.0F, -10.0F, 34.0F, 18.0F, 18.0F, 12.0F, new Dilation(0.0F))
                .uv(535, 1207).cuboid(8.0F, -8.0F, 78.0F, 8.0F, 8.0F, 20.0F, new Dilation(0.0F))
                .uv(766, 1207).cuboid(0.0F, -8.0F, 66.0F, 16.0F, 16.0F, 12.0F, new Dilation(0.0F))
                .uv(487, 1176).cuboid(0.0F, -10.0F, 98.0F, 18.0F, 18.0F, 12.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, -8.0F, 0.0F));

        ModelPartData top_right = actual_barrel.addChild("top_right", ModelPartBuilder.create().uv(548, 1176).cuboid(-18.0F, -10.0F, -94.0F, 18.0F, 18.0F, 12.0F, new Dilation(0.0F))
                .uv(823, 1207).cuboid(-16.0F, -8.0F, -82.0F, 8.0F, 8.0F, 20.0F, new Dilation(0.0F))
                .uv(1168, 1207).cuboid(-16.0F, -8.0F, -50.0F, 8.0F, 8.0F, 20.0F, new Dilation(0.0F))
                .uv(1208, 550).cuboid(-16.0F, -8.0F, -62.0F, 16.0F, 16.0F, 12.0F, new Dilation(0.0F))
                .uv(1208, 579).cuboid(-16.0F, -8.0F, -18.0F, 8.0F, 8.0F, 20.0F, new Dilation(0.0F))
                .uv(985, 1177).cuboid(-18.0F, -10.0F, -30.0F, 18.0F, 18.0F, 12.0F, new Dilation(0.0F))
                .uv(1208, 608).cuboid(-16.0F, -8.0F, 14.0F, 8.0F, 8.0F, 20.0F, new Dilation(0.0F))
                .uv(1208, 637).cuboid(-16.0F, -8.0F, 2.0F, 16.0F, 16.0F, 12.0F, new Dilation(0.0F))
                .uv(1208, 666).cuboid(-16.0F, -8.0F, 46.0F, 8.0F, 8.0F, 20.0F, new Dilation(0.0F))
                .uv(1046, 1177).cuboid(-18.0F, -10.0F, 34.0F, 18.0F, 18.0F, 12.0F, new Dilation(0.0F))
                .uv(1208, 695).cuboid(-16.0F, -8.0F, 78.0F, 8.0F, 8.0F, 20.0F, new Dilation(0.0F))
                .uv(1208, 724).cuboid(-16.0F, -8.0F, 66.0F, 16.0F, 16.0F, 12.0F, new Dilation(0.0F))
                .uv(1107, 1177).cuboid(-18.0F, -10.0F, 98.0F, 18.0F, 18.0F, 12.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, -8.0F, 0.0F));

        ModelPartData back = body.addChild("back", ModelPartBuilder.create().uv(962, 0).cuboid(-56.0F, -30.0F, -58.0F, 112.0F, 30.0F, 149.0F, new Dilation(0.01F))
                .uv(962, 971).cuboid(-32.0F, -30.0F, 91.0F, 64.0F, 30.0F, 24.0F, new Dilation(0.01F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData cube_r41 = back.addChild("cube_r41", ModelPartBuilder.create().uv(1091, 730).cuboid(-34.0F, -15.0F, -24.0F, 34.0F, 30.0F, 24.0F, new Dilation(-0.01F)), ModelTransform.of(-32.0F, -15.0F, 115.0F, 0.0F, -0.7854F, 0.0F));

        ModelPartData cube_r42 = back.addChild("cube_r42", ModelPartBuilder.create().uv(1091, 675).cuboid(0.0F, -15.0F, -24.0F, 34.0F, 30.0F, 24.0F, new Dilation(-0.01F)), ModelTransform.of(32.0F, -15.0F, 115.0F, 0.0F, 0.7854F, 0.0F));

        ModelPartData legs = starpiercer.addChild("legs", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData left_leg = legs.addChild("left_leg", ModelPartBuilder.create().uv(962, 553).cuboid(56.0F, -102.0F, -24.0F, 16.0F, 102.0F, 48.0F, new Dilation(0.1F))
                .uv(176, 1103).cuboid(56.0F, -123.0F, -11.0F, 16.0F, 21.0F, 22.0F, new Dilation(0.1F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData cube_r43 = left_leg.addChild("cube_r43", ModelPartBuilder.create().uv(543, 1109).cuboid(-8.0F, 0.0F, 0.0F, 16.0F, 12.0F, 25.0F, new Dilation(-0.01F)), ModelTransform.of(64.0F, -123.0F, 11.0F, -1.0123F, 0.0F, 0.0F));

        ModelPartData cube_r44 = left_leg.addChild("cube_r44", ModelPartBuilder.create().uv(460, 1109).cuboid(-8.0F, 0.0F, -25.0F, 16.0F, 12.0F, 25.0F, new Dilation(-0.01F)), ModelTransform.of(64.0F, -123.0F, -11.0F, 1.0123F, 0.0F, 0.0F));

        ModelPartData right_leg = legs.addChild("right_leg", ModelPartBuilder.create().uv(962, 704).cuboid(-72.0F, -102.0F, -24.0F, 16.0F, 102.0F, 48.0F, new Dilation(0.1F))
                .uv(928, 1111).cuboid(-72.0F, -123.0F, -11.0F, 16.0F, 21.0F, 22.0F, new Dilation(0.1F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData cube_r45 = right_leg.addChild("cube_r45", ModelPartBuilder.create().uv(626, 1145).cuboid(-8.0F, 0.0F, 0.0F, 16.0F, 12.0F, 25.0F, new Dilation(-0.01F)), ModelTransform.of(-64.0F, -123.0F, 11.0F, -1.0123F, 0.0F, 0.0F));

        ModelPartData cube_r46 = right_leg.addChild("cube_r46", ModelPartBuilder.create().uv(1139, 971).cuboid(-8.0F, 0.0F, -25.0F, 16.0F, 12.0F, 25.0F, new Dilation(-0.01F)), ModelTransform.of(-64.0F, -123.0F, -11.0F, 1.0123F, 0.0F, 0.0F));
        return TexturedModelData.of(modelData, 2048, 2048);
	}
	@Override
	public void setAngles(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

	}

	@Override
	public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, int color) {
		this.getMainPart().render(matrices, vertices, light, overlay, color);
	}

	public ModelPart getMainPart() {
		return this.starpiercer;
	}

	public ModelPart getBarrel() {
		return this.barrel;
	}
}