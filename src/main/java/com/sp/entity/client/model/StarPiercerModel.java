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

		ModelPartData left = front.addChild("left", ModelPartBuilder.create().uv(480, 518).cuboid(10.0F, -22.5F, -117.0F, 6.0F, 24.0F, 234.0F, new Dilation(0.0F))
		.uv(860, 1034).cuboid(-28.9F, -25.5F, -142.0F, 22.0F, 30.0F, 20.0F, new Dilation(0.0F)), ModelTransform.pivot(61.9F, -4.5F, 18.0F));

		ModelPartData cube_r1 = left.addChild("cube_r1", ModelPartBuilder.create().uv(0, 1034).cuboid(-30.0F, -15.0F, 0.0F, 30.0F, 30.0F, 34.0F, new Dilation(0.0F)), ModelTransform.of(-6.9F, -10.5F, -142.0F, 0.0F, 0.7418F, 0.0F));

		ModelPartData cube_r2 = left.addChild("cube_r2", ModelPartBuilder.create().uv(0, 259).cuboid(-1.5F, -12.0F, -117.0F, 6.0F, 25.0F, 234.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.5708F));

		ModelPartData cube_r3 = left.addChild("cube_r3", ModelPartBuilder.create().uv(0, 0).cuboid(-1.5F, -12.0F, -117.0F, 6.0F, 25.0F, 234.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -21.0F, 0.0F, 0.0F, 0.0F, -1.5708F));

		ModelPartData cube_r4 = left.addChild("cube_r4", ModelPartBuilder.create().uv(0, 518).cuboid(-1.5F, -12.0F, -117.0F, 6.0F, 24.0F, 234.0F, new Dilation(0.0F)), ModelTransform.of(-11.5F, -10.5F, 0.0F, 0.0F, 0.0F, -3.1416F));

		ModelPartData ribs = left.addChild("ribs", ModelPartBuilder.create().uv(744, 1034).cuboid(-14.9F, -28.5F, 93.0F, 34.0F, 36.0F, 24.0F, new Dilation(0.0F))
		.uv(1084, 1017).cuboid(-14.9F, -28.5F, 48.0F, 34.0F, 36.0F, 24.0F, new Dilation(0.0F))
		.uv(1084, 1077).cuboid(-14.9F, -28.5F, 3.0F, 34.0F, 36.0F, 24.0F, new Dilation(0.0F))
		.uv(1088, 549).cuboid(-14.9F, -28.5F, -42.0F, 34.0F, 36.0F, 24.0F, new Dilation(0.0F))
		.uv(1088, 609).cuboid(-14.9F, -28.5F, -87.0F, 34.0F, 36.0F, 24.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

		ModelPartData right = front.addChild("right", ModelPartBuilder.create().uv(0, 776).cuboid(-16.0F, -1.5F, -117.0F, 6.0F, 24.0F, 234.0F, new Dilation(0.0F))
		.uv(116, 1102).cuboid(6.9F, -4.5F, -142.0F, 22.0F, 30.0F, 20.0F, new Dilation(0.0F)), ModelTransform.pivot(-17.9F, -25.5F, 18.0F));

		ModelPartData cube_r5 = right.addChild("cube_r5", ModelPartBuilder.create().uv(128, 1034).cuboid(0.0F, -15.0F, 0.0F, 30.0F, 30.0F, 34.0F, new Dilation(0.0F)), ModelTransform.of(6.9F, 10.5F, -142.0F, 0.0F, -0.7418F, 0.0F));

		ModelPartData cube_r6 = right.addChild("cube_r6", ModelPartBuilder.create().uv(480, 0).cuboid(-4.5F, -12.0F, -117.0F, 6.0F, 25.0F, 234.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.5708F));

		ModelPartData cube_r7 = right.addChild("cube_r7", ModelPartBuilder.create().uv(480, 776).cuboid(-4.5F, -12.0F, -117.0F, 6.0F, 24.0F, 234.0F, new Dilation(0.0F)), ModelTransform.of(11.5F, 10.5F, 0.0F, 0.0F, 0.0F, 3.1416F));

		ModelPartData cube_r8 = right.addChild("cube_r8", ModelPartBuilder.create().uv(480, 259).cuboid(-4.5F, -12.0F, -117.0F, 6.0F, 25.0F, 234.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 21.0F, 0.0F, 0.0F, 0.0F, -1.5708F));

		ModelPartData ribs2 = right.addChild("ribs2", ModelPartBuilder.create().uv(1088, 669).cuboid(-98.9F, -28.5F, 93.0F, 34.0F, 36.0F, 24.0F, new Dilation(0.0F))
		.uv(1088, 729).cuboid(-98.9F, -28.5F, 48.0F, 34.0F, 36.0F, 24.0F, new Dilation(0.0F))
		.uv(1088, 789).cuboid(-98.9F, -28.5F, 3.0F, 34.0F, 36.0F, 24.0F, new Dilation(0.0F))
		.uv(744, 1094).cuboid(-98.9F, -28.5F, -42.0F, 34.0F, 36.0F, 24.0F, new Dilation(0.0F))
		.uv(256, 1095).cuboid(-98.9F, -28.5F, -87.0F, 34.0F, 36.0F, 24.0F, new Dilation(0.0F)), ModelTransform.pivot(79.8F, 21.0F, 0.0F));

		ModelPartData barrelsupport = body.addChild("barrelsupport", ModelPartBuilder.create().uv(960, 403).cuboid(-23.0F, -40.0F, -58.0F, 46.0F, 10.0F, 63.0F, new Dilation(0.0F))
		.uv(960, 476).cuboid(-23.0F, 0.0F, -58.0F, 46.0F, 10.0F, 63.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

		ModelPartData cube_r9 = barrelsupport.addChild("cube_r9", ModelPartBuilder.create().uv(960, 906).cuboid(-23.0F, -10.0F, 0.0F, 46.0F, 10.0F, 47.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 10.0F, 5.0F, 0.2182F, 0.0F, 0.0F));

		ModelPartData cube_r10 = barrelsupport.addChild("cube_r10", ModelPartBuilder.create().uv(960, 849).cuboid(-23.0F, 0.0F, 0.0F, 46.0F, 10.0F, 47.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -40.0F, 5.0F, -0.2182F, 0.0F, 0.0F));

		ModelPartData details = body.addChild("details", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

		ModelPartData pipes = details.addChild("pipes", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

		ModelPartData set1 = pipes.addChild("set1", ModelPartBuilder.create().uv(1136, 1012).cuboid(23.0F, -32.0F, -38.0F, 17.0F, 2.0F, 2.0F, new Dilation(1.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

		ModelPartData cube_r11 = set1.addChild("cube_r11", ModelPartBuilder.create().uv(944, 1034).cuboid(-1.0F, 1.0F, 1.0F, 2.0F, 2.0F, 3.0F, new Dilation(1.0F)), ModelTransform.of(43.0F, -33.0F, 36.0F, -0.7854F, 0.0F, 0.0F));

		ModelPartData cube_r12 = set1.addChild("cube_r12", ModelPartBuilder.create().uv(860, 1101).cuboid(-1.0F, -2.0F, -1.0F, 73.0F, 2.0F, 2.0F, new Dilation(1.0F)), ModelTransform.of(43.0F, -30.0F, -37.0F, 0.0F, -1.5708F, 0.0F));

		ModelPartData set2 = pipes.addChild("set2", ModelPartBuilder.create().uv(1146, 958).cuboid(23.0F, -32.0F, -34.0F, 17.0F, 2.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

		ModelPartData cube_r13 = set2.addChild("cube_r13", ModelPartBuilder.create().uv(944, 1039).cuboid(-1.0F, 1.0F, 1.0F, 2.0F, 2.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(39.0F, -33.4F, 36.0F, -0.7854F, 0.0F, 0.0F));

		ModelPartData cube_r14 = set2.addChild("cube_r14", ModelPartBuilder.create().uv(860, 1105).cuboid(-1.0F, -2.0F, -1.0F, 70.0F, 2.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(39.0F, -30.0F, -33.0F, 0.0F, -1.5708F, 0.0F));

		ModelPartData set3 = pipes.addChild("set3", ModelPartBuilder.create().uv(82, 1145).cuboid(23.0F, -32.0F, -31.0F, 14.0F, 2.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

		ModelPartData cube_r15 = set3.addChild("cube_r15", ModelPartBuilder.create().uv(944, 1044).cuboid(-1.0F, 1.0F, 1.0F, 2.0F, 2.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(36.0F, -33.4F, 38.0F, -0.7854F, 0.0F, 0.0F));

		ModelPartData cube_r16 = set3.addChild("cube_r16", ModelPartBuilder.create().uv(860, 1109).cuboid(-1.0F, -2.0F, -1.0F, 69.0F, 2.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(36.0F, -30.0F, -30.0F, 0.0F, -1.5708F, 0.0F));

		ModelPartData set4 = pipes.addChild("set4", ModelPartBuilder.create().uv(1184, 958).cuboid(20.0F, -32.0F, -28.0F, 14.0F, 2.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

		ModelPartData cube_r17 = set4.addChild("cube_r17", ModelPartBuilder.create().uv(116, 1098).cuboid(1.0F, -2.0F, -1.0F, 66.0F, 2.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(33.0F, -30.0F, -27.0F, 0.0F, -1.5708F, 0.0F));

		ModelPartData cube_r18 = set4.addChild("cube_r18", ModelPartBuilder.create().uv(944, 1049).cuboid(-1.0F, 1.0F, 1.0F, 2.0F, 2.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(33.0F, -33.4F, 40.0F, -0.7854F, 0.0F, 0.0F));

		ModelPartData set5 = pipes.addChild("set5", ModelPartBuilder.create().uv(1062, 1101).cuboid(20.0F, -32.0F, -23.0F, 9.0F, 2.0F, 2.0F, new Dilation(2.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

		ModelPartData cube_r19 = set5.addChild("cube_r19", ModelPartBuilder.create().uv(860, 1113).cuboid(3.0F, -2.0F, -1.0F, 60.0F, 2.0F, 2.0F, new Dilation(2.0F)), ModelTransform.of(28.0F, -30.0F, -20.0F, 0.0F, -1.5708F, 0.0F));

		ModelPartData cube_r20 = set5.addChild("cube_r20", ModelPartBuilder.create().uv(944, 1054).cuboid(-1.0F, 1.0F, 1.0F, 2.0F, 2.0F, 3.0F, new Dilation(2.0F)), ModelTransform.of(28.0F, -32.6F, 45.0F, -0.7854F, 0.0F, 0.0F));

		ModelPartData set6 = pipes.addChild("set6", ModelPartBuilder.create().uv(564, 1178).cuboid(-11.0F, -49.0F, -39.0F, 6.0F, 6.0F, 23.0F, new Dilation(1.0F))
		.uv(714, 1134).cuboid(-11.0F, -49.0F, -47.0F, 6.0F, 8.0F, 6.0F, new Dilation(1.0F))
		.uv(1178, 523).cuboid(-11.0F, -49.0F, -14.0F, 6.0F, 8.0F, 6.0F, new Dilation(1.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

		ModelPartData set7 = pipes.addChild("set7", ModelPartBuilder.create().uv(622, 1178).cuboid(5.0F, -49.0F, -39.0F, 6.0F, 6.0F, 23.0F, new Dilation(1.0F))
		.uv(56, 1182).cuboid(5.0F, -49.0F, -47.0F, 6.0F, 8.0F, 6.0F, new Dilation(1.0F))
		.uv(114, 1208).cuboid(5.0F, -49.0F, -14.0F, 6.0F, 8.0F, 6.0F, new Dilation(1.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

		ModelPartData set8 = pipes.addChild("set8", ModelPartBuilder.create().uv(912, 1084).cuboid(59.0F, -19.0F, -74.0F, 10.0F, 8.0F, 8.0F, new Dilation(0.0F))
		.uv(1204, 831).cuboid(59.0F, -19.0F, -119.0F, 10.0F, 8.0F, 8.0F, new Dilation(0.0F))
		.uv(272, 1050).cuboid(61.0F, -19.0F, -111.0F, 8.0F, 8.0F, 37.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

		ModelPartData set9 = pipes.addChild("set9", ModelPartBuilder.create().uv(922, 1208).cuboid(59.0F, -19.0F, -74.0F, 10.0F, 8.0F, 8.0F, new Dilation(0.0F))
		.uv(958, 1208).cuboid(59.0F, -19.0F, -119.0F, 10.0F, 8.0F, 8.0F, new Dilation(0.0F))
		.uv(516, 1050).cuboid(61.0F, -19.0F, -111.0F, 8.0F, 8.0F, 37.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, -135.0F));

		ModelPartData set10 = pipes.addChild("set10", ModelPartBuilder.create().uv(994, 1208).cuboid(-69.0F, -19.0F, -74.0F, 10.0F, 8.0F, 8.0F, new Dilation(0.0F))
		.uv(1030, 1208).cuboid(-69.0F, -19.0F, -119.0F, 10.0F, 8.0F, 8.0F, new Dilation(0.0F))
		.uv(638, 1050).cuboid(-69.0F, -19.0F, -111.0F, 8.0F, 8.0F, 37.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, -135.0F));

		ModelPartData set11 = pipes.addChild("set11", ModelPartBuilder.create().uv(492, 1208).cuboid(-69.0F, -19.0F, -74.0F, 10.0F, 8.0F, 8.0F, new Dilation(0.0F))
		.uv(528, 1208).cuboid(-69.0F, -19.0F, -119.0F, 10.0F, 8.0F, 8.0F, new Dilation(0.0F))
		.uv(394, 1050).cuboid(-69.0F, -19.0F, -111.0F, 8.0F, 8.0F, 37.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

		ModelPartData set12 = pipes.addChild("set12", ModelPartBuilder.create().uv(0, 1210).cuboid(-3.0F, -1.2559F, 7.4512F, 14.0F, 2.0F, 2.0F, new Dilation(0.0F))
		.uv(620, 1207).cuboid(-3.0F, -1.2559F, -8.5488F, 2.0F, 2.0F, 18.0F, new Dilation(0.0F)), ModelTransform.pivot(8.0F, -30.7441F, 65.5488F));

		ModelPartData cube_r21 = set12.addChild("cube_r21", ModelPartBuilder.create().uv(944, 1059).cuboid(-1.0F, 0.0F, -3.0F, 2.0F, 2.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(-2.0F, -1.2559F, -8.5488F, 0.7854F, 0.0F, 0.0F));

		ModelPartData set13 = pipes.addChild("set13", ModelPartBuilder.create().uv(138, 1210).cuboid(-11.0F, -1.2559F, 7.4512F, 14.0F, 2.0F, 2.0F, new Dilation(0.0F))
		.uv(56, 1208).cuboid(1.0F, -1.2559F, -8.5488F, 2.0F, 2.0F, 18.0F, new Dilation(0.0F)), ModelTransform.pivot(-8.0F, -30.7441F, 65.5488F));

		ModelPartData cube_r22 = set13.addChild("cube_r22", ModelPartBuilder.create().uv(944, 1064).cuboid(-1.0F, 0.0F, -3.0F, 2.0F, 2.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(2.0F, -1.2559F, -8.5488F, 0.7854F, 0.0F, 0.0F));

		ModelPartData set14 = pipes.addChild("set14", ModelPartBuilder.create().uv(324, 1155).cuboid(-44.0F, -37.0F, 23.0F, 6.0F, 6.0F, 15.0F, new Dilation(1.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

		ModelPartData cube_r23 = set14.addChild("cube_r23", ModelPartBuilder.create().uv(1066, 1208).cuboid(-3.0F, 1.0F, 1.0F, 6.0F, 6.0F, 10.0F, new Dilation(1.0F)), ModelTransform.of(-41.0F, -38.0F, 39.0F, -0.7854F, 0.0F, 0.0F));

		ModelPartData sights = details.addChild("sights", ModelPartBuilder.create().uv(960, 1017).cuboid(-50.0F, -134.0F, -22.0F, 18.0F, 40.0F, 44.0F, new Dilation(0.0F))
		.uv(1098, 1208).cuboid(-48.0F, -132.0F, -24.0F, 14.0F, 13.0F, 2.0F, new Dilation(0.0F))
		.uv(0, 1098).cuboid(-48.0F, -137.0F, -22.0F, 14.0F, 3.0F, 44.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 83.0F, 0.0F));

		ModelPartData exhaust = details.addChild("exhaust", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

		ModelPartData left_exhaust = exhaust.addChild("left_exhaust", ModelPartBuilder.create().uv(1146, 886).cuboid(19.0F, -42.0F, 68.0F, 16.0F, 12.0F, 24.0F, new Dilation(0.0F))
		.uv(204, 1130).cuboid(19.0F, -55.0F, 82.0F, 16.0F, 13.0F, 10.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

		ModelPartData cube_r24 = left_exhaust.addChild("cube_r24", ModelPartBuilder.create().uv(942, 1148).cuboid(-7.0F, 0.0F, -19.0F, 14.0F, 13.0F, 19.0F, new Dilation(0.0F)), ModelTransform.of(27.0F, -55.0F, 82.0F, 0.7418F, 0.0F, 0.0F));

		ModelPartData right_exhaust = exhaust.addChild("right_exhaust", ModelPartBuilder.create().uv(1146, 922).cuboid(-35.0F, -42.0F, 68.0F, 16.0F, 12.0F, 24.0F, new Dilation(0.0F))
		.uv(1200, 1112).cuboid(-35.0F, -55.0F, 82.0F, 16.0F, 13.0F, 10.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

		ModelPartData cube_r25 = right_exhaust.addChild("cube_r25", ModelPartBuilder.create().uv(1008, 1148).cuboid(-7.0F, 0.0F, -19.0F, 14.0F, 13.0F, 19.0F, new Dilation(0.0F)), ModelTransform.of(-27.0F, -55.0F, 82.0F, 0.7418F, 0.0F, 0.0F));

		ModelPartData vents = details.addChild("vents", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

		ModelPartData cube_r26 = vents.addChild("cube_r26", ModelPartBuilder.create().uv(1010, 1101).cuboid(-12.0F, 0.0F, -1.0F, 24.0F, 2.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 3.0F, 33.4F, 0.2182F, 0.0F, 0.0F));

		ModelPartData cube_r27 = vents.addChild("cube_r27", ModelPartBuilder.create().uv(1136, 1000).cuboid(-12.0F, 0.0F, -1.0F, 24.0F, 2.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 4.0F, 28.4F, 0.2182F, 0.0F, 0.0F));

		ModelPartData cube_r28 = vents.addChild("cube_r28", ModelPartBuilder.create().uv(1136, 1004).cuboid(-12.0F, 0.0F, -1.0F, 24.0F, 2.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 5.0F, 23.4F, 0.2182F, 0.0F, 0.0F));

		ModelPartData cube_r29 = vents.addChild("cube_r29", ModelPartBuilder.create().uv(1136, 1008).cuboid(-12.0F, 0.0F, -1.0F, 24.0F, 2.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 6.0F, 18.4F, 0.2182F, 0.0F, 0.0F));

		ModelPartData cube_r30 = vents.addChild("cube_r30", ModelPartBuilder.create().uv(860, 1096).cuboid(-12.0F, -2.0F, -1.0F, 24.0F, 2.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -33.0F, 33.4F, -0.2182F, 0.0F, 0.0F));

		ModelPartData cube_r31 = vents.addChild("cube_r31", ModelPartBuilder.create().uv(860, 1092).cuboid(-12.0F, -2.0F, -1.0F, 24.0F, 2.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -34.0F, 28.4F, -0.2182F, 0.0F, 0.0F));

		ModelPartData cube_r32 = vents.addChild("cube_r32", ModelPartBuilder.create().uv(860, 1088).cuboid(-12.0F, -2.0F, -1.0F, 24.0F, 2.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -35.0F, 23.4F, -0.2182F, 0.0F, 0.0F));

		ModelPartData cube_r33 = vents.addChild("cube_r33", ModelPartBuilder.create().uv(860, 1084).cuboid(-12.0F, -2.0F, -1.0F, 24.0F, 2.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -36.0F, 18.4F, -0.2182F, 0.0F, 0.0F));

		ModelPartData ribs3 = details.addChild("ribs3", ModelPartBuilder.create().uv(714, 1095).cuboid(-2.0F, -32.0F, 112.0F, 4.0F, 34.0F, 5.0F, new Dilation(0.0F))
		.uv(474, 1207).cuboid(22.0F, -32.0F, 112.0F, 4.0F, 34.0F, 5.0F, new Dilation(0.0F))
		.uv(904, 1184).cuboid(10.0F, -32.0F, 112.0F, 4.0F, 34.0F, 5.0F, new Dilation(0.0F))
		.uv(96, 1208).cuboid(-14.0F, -32.0F, 112.0F, 4.0F, 34.0F, 5.0F, new Dilation(0.0F))
		.uv(660, 1207).cuboid(-26.0F, -32.0F, 112.0F, 4.0F, 34.0F, 5.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

		ModelPartData barrel = body.addChild("barrel", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, -14.0F, 0.0F));

		ModelPartData muzzle = barrel.addChild("muzzle", ModelPartBuilder.create().uv(604, 1095).cuboid(-20.0F, -20.0F, -7.5F, 40.0F, 40.0F, 15.0F, new Dilation(0.0F))
		.uv(942, 1117).cuboid(-8.0F, -19.0F, -22.5F, 16.0F, 13.0F, 15.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, -269.5F));

		ModelPartData cube_r34 = muzzle.addChild("cube_r34", ModelPartBuilder.create().uv(82, 1152).cuboid(-8.0F, -19.0F, -9.0F, 16.0F, 13.0F, 15.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, -13.5F, 0.0F, 0.0F, -0.7854F));

		ModelPartData cube_r35 = muzzle.addChild("cube_r35", ModelPartBuilder.create().uv(682, 1150).cuboid(-8.0F, -19.0F, -9.0F, 16.0F, 13.0F, 15.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, -13.5F, 0.0F, 0.0F, -1.5708F));

		ModelPartData cube_r36 = muzzle.addChild("cube_r36", ModelPartBuilder.create().uv(620, 1150).cuboid(-8.0F, -19.0F, -9.0F, 16.0F, 13.0F, 15.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, -13.5F, 0.0F, 0.0F, -2.3562F));

		ModelPartData cube_r37 = muzzle.addChild("cube_r37", ModelPartBuilder.create().uv(558, 1150).cuboid(-8.0F, -19.0F, -9.0F, 16.0F, 13.0F, 15.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, -13.5F, 0.0F, 0.0F, -3.1416F));

		ModelPartData cube_r38 = muzzle.addChild("cube_r38", ModelPartBuilder.create().uv(496, 1149).cuboid(-8.0F, -19.0F, -9.0F, 16.0F, 13.0F, 15.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, -13.5F, 0.0F, 0.0F, 2.3562F));

		ModelPartData cube_r39 = muzzle.addChild("cube_r39", ModelPartBuilder.create().uv(434, 1149).cuboid(-8.0F, -19.0F, -9.0F, 16.0F, 13.0F, 15.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, -13.5F, 0.0F, 0.0F, 1.5708F));

		ModelPartData cube_r40 = muzzle.addChild("cube_r40", ModelPartBuilder.create().uv(372, 1149).cuboid(-8.0F, -19.0F, -9.0F, 16.0F, 13.0F, 15.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, -13.5F, 0.0F, 0.0F, 0.7854F));

		ModelPartData actual_barrel = barrel.addChild("actual_barrel", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, -168.0F));

		ModelPartData light3 = actual_barrel.addChild("light3", ModelPartBuilder.create().uv(960, 179).cuboid(-8.0F, -16.0F, -96.0F, 16.0F, 16.0F, 208.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 8.0F, 0.0F));

		ModelPartData bottom_right = actual_barrel.addChild("bottom_right", ModelPartBuilder.create().uv(144, 1152).cuboid(-18.0F, -8.0F, -94.0F, 18.0F, 18.0F, 12.0F, new Dilation(0.0F))
		.uv(200, 1102).cuboid(-16.0F, 0.0F, -82.0F, 8.0F, 8.0F, 20.0F, new Dilation(0.0F))
		.uv(680, 1178).cuboid(-16.0F, 0.0F, -50.0F, 8.0F, 8.0F, 20.0F, new Dilation(0.0F))
		.uv(82, 1180).cuboid(-16.0F, -8.0F, -62.0F, 16.0F, 16.0F, 12.0F, new Dilation(0.0F))
		.uv(924, 1180).cuboid(-16.0F, 0.0F, -18.0F, 8.0F, 8.0F, 20.0F, new Dilation(0.0F))
		.uv(744, 1154).cuboid(-18.0F, -8.0F, -30.0F, 18.0F, 18.0F, 12.0F, new Dilation(0.0F))
		.uv(980, 1180).cuboid(-16.0F, 0.0F, 14.0F, 8.0F, 8.0F, 20.0F, new Dilation(0.0F))
		.uv(1036, 1180).cuboid(-16.0F, -8.0F, 2.0F, 16.0F, 16.0F, 12.0F, new Dilation(0.0F))
		.uv(1092, 1180).cuboid(-16.0F, 0.0F, 46.0F, 8.0F, 8.0F, 20.0F, new Dilation(0.0F))
		.uv(804, 1154).cuboid(-18.0F, -8.0F, 34.0F, 18.0F, 18.0F, 12.0F, new Dilation(0.0F))
		.uv(0, 1182).cuboid(-16.0F, 0.0F, 78.0F, 8.0F, 8.0F, 20.0F, new Dilation(0.0F))
		.uv(138, 1182).cuboid(-16.0F, -8.0F, 66.0F, 16.0F, 16.0F, 12.0F, new Dilation(0.0F))
		.uv(864, 1154).cuboid(-18.0F, -8.0F, 98.0F, 18.0F, 18.0F, 12.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 8.0F, 0.0F));

		ModelPartData bottom_left = actual_barrel.addChild("bottom_left", ModelPartBuilder.create().uv(204, 1155).cuboid(0.0F, -8.0F, -94.0F, 18.0F, 18.0F, 12.0F, new Dilation(0.0F))
		.uv(736, 1184).cuboid(8.0F, 0.0F, -82.0F, 8.0F, 8.0F, 20.0F, new Dilation(0.0F))
		.uv(792, 1184).cuboid(8.0F, 0.0F, -50.0F, 8.0F, 8.0F, 20.0F, new Dilation(0.0F))
		.uv(848, 1184).cuboid(0.0F, -8.0F, -62.0F, 16.0F, 16.0F, 12.0F, new Dilation(0.0F))
		.uv(194, 1185).cuboid(8.0F, 0.0F, -18.0F, 8.0F, 8.0F, 20.0F, new Dilation(0.0F))
		.uv(264, 1155).cuboid(0.0F, -8.0F, -30.0F, 18.0F, 18.0F, 12.0F, new Dilation(0.0F))
		.uv(250, 1185).cuboid(8.0F, 0.0F, 14.0F, 8.0F, 8.0F, 20.0F, new Dilation(0.0F))
		.uv(1148, 1197).cuboid(0.0F, -8.0F, 2.0F, 16.0F, 16.0F, 12.0F, new Dilation(0.0F))
		.uv(1200, 1000).cuboid(8.0F, 0.0F, 46.0F, 8.0F, 8.0F, 20.0F, new Dilation(0.0F))
		.uv(1156, 1137).cuboid(0.0F, -8.0F, 34.0F, 18.0F, 18.0F, 12.0F, new Dilation(0.0F))
		.uv(1200, 1028).cuboid(8.0F, 0.0F, 78.0F, 8.0F, 8.0F, 20.0F, new Dilation(0.0F))
		.uv(1200, 1056).cuboid(0.0F, -8.0F, 66.0F, 16.0F, 16.0F, 12.0F, new Dilation(0.0F))
		.uv(1156, 1167).cuboid(0.0F, -8.0F, 98.0F, 18.0F, 18.0F, 12.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 8.0F, 0.0F));

		ModelPartData top_left = actual_barrel.addChild("top_left", ModelPartBuilder.create().uv(324, 1177).cuboid(0.0F, -10.0F, -94.0F, 18.0F, 18.0F, 12.0F, new Dilation(0.0F))
		.uv(1200, 1084).cuboid(8.0F, -8.0F, -82.0F, 8.0F, 8.0F, 20.0F, new Dilation(0.0F))
		.uv(1204, 523).cuboid(8.0F, -8.0F, -50.0F, 8.0F, 8.0F, 20.0F, new Dilation(0.0F))
		.uv(1204, 551).cuboid(0.0F, -8.0F, -62.0F, 16.0F, 16.0F, 12.0F, new Dilation(0.0F))
		.uv(1204, 579).cuboid(8.0F, -8.0F, -18.0F, 8.0F, 8.0F, 20.0F, new Dilation(0.0F))
		.uv(384, 1177).cuboid(0.0F, -10.0F, -30.0F, 18.0F, 18.0F, 12.0F, new Dilation(0.0F))
		.uv(1204, 607).cuboid(8.0F, -8.0F, 14.0F, 8.0F, 8.0F, 20.0F, new Dilation(0.0F))
		.uv(1204, 635).cuboid(0.0F, -8.0F, 2.0F, 16.0F, 16.0F, 12.0F, new Dilation(0.0F))
		.uv(1204, 663).cuboid(8.0F, -8.0F, 46.0F, 8.0F, 8.0F, 20.0F, new Dilation(0.0F))
		.uv(444, 1177).cuboid(0.0F, -10.0F, 34.0F, 18.0F, 18.0F, 12.0F, new Dilation(0.0F))
		.uv(1204, 691).cuboid(8.0F, -8.0F, 78.0F, 8.0F, 8.0F, 20.0F, new Dilation(0.0F))
		.uv(1204, 719).cuboid(0.0F, -8.0F, 66.0F, 16.0F, 16.0F, 12.0F, new Dilation(0.0F))
		.uv(1178, 403).cuboid(0.0F, -10.0F, 98.0F, 18.0F, 18.0F, 12.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, -8.0F, 0.0F));

		ModelPartData top_right = actual_barrel.addChild("top_right", ModelPartBuilder.create().uv(1178, 433).cuboid(-18.0F, -10.0F, -94.0F, 18.0F, 18.0F, 12.0F, new Dilation(0.0F))
		.uv(1204, 747).cuboid(-16.0F, -8.0F, -82.0F, 8.0F, 8.0F, 20.0F, new Dilation(0.0F))
		.uv(1204, 775).cuboid(-16.0F, -8.0F, -50.0F, 8.0F, 8.0F, 20.0F, new Dilation(0.0F))
		.uv(1204, 803).cuboid(-16.0F, -8.0F, -62.0F, 16.0F, 16.0F, 12.0F, new Dilation(0.0F))
		.uv(1204, 1197).cuboid(-16.0F, -8.0F, -18.0F, 8.0F, 8.0F, 20.0F, new Dilation(0.0F))
		.uv(1178, 463).cuboid(-18.0F, -10.0F, -30.0F, 18.0F, 18.0F, 12.0F, new Dilation(0.0F))
		.uv(680, 1206).cuboid(-16.0F, -8.0F, 14.0F, 8.0F, 8.0F, 20.0F, new Dilation(0.0F))
		.uv(306, 1207).cuboid(-16.0F, -8.0F, 2.0F, 16.0F, 16.0F, 12.0F, new Dilation(0.0F))
		.uv(362, 1207).cuboid(-16.0F, -8.0F, 46.0F, 8.0F, 8.0F, 20.0F, new Dilation(0.0F))
		.uv(1178, 493).cuboid(-18.0F, -10.0F, 34.0F, 18.0F, 18.0F, 12.0F, new Dilation(0.0F))
		.uv(418, 1207).cuboid(-16.0F, -8.0F, 78.0F, 8.0F, 8.0F, 20.0F, new Dilation(0.0F))
		.uv(564, 1207).cuboid(-16.0F, -8.0F, 66.0F, 16.0F, 16.0F, 12.0F, new Dilation(0.0F))
		.uv(504, 1178).cuboid(-18.0F, -10.0F, 98.0F, 18.0F, 18.0F, 12.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, -8.0F, 0.0F));

		ModelPartData back = body.addChild("back", ModelPartBuilder.create().uv(960, 0).cuboid(-56.0F, -30.0F, -58.0F, 112.0F, 30.0F, 149.0F, new Dilation(0.0F))
		.uv(960, 963).cuboid(-32.0F, -30.0F, 91.0F, 64.0F, 30.0F, 24.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

		ModelPartData cube_r41 = back.addChild("cube_r41", ModelPartBuilder.create().uv(488, 1095).cuboid(-34.0F, -15.0F, -24.0F, 34.0F, 30.0F, 24.0F, new Dilation(0.0F)), ModelTransform.of(-32.0F, -15.0F, 115.0F, 0.0F, -0.7854F, 0.0F));

		ModelPartData cube_r42 = back.addChild("cube_r42", ModelPartBuilder.create().uv(372, 1095).cuboid(0.0F, -15.0F, -24.0F, 34.0F, 30.0F, 24.0F, new Dilation(0.0F)), ModelTransform.of(32.0F, -15.0F, 115.0F, 0.0F, 0.7854F, 0.0F));

		ModelPartData legs = starpiercer.addChild("legs", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

		ModelPartData left_leg = legs.addChild("left_leg", ModelPartBuilder.create().uv(960, 549).cuboid(56.0F, -102.0F, -24.0F, 16.0F, 102.0F, 48.0F, new Dilation(0.0F))
		.uv(1004, 1105).cuboid(56.0F, -123.0F, -11.0F, 16.0F, 21.0F, 22.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

		ModelPartData cube_r43 = left_leg.addChild("cube_r43", ModelPartBuilder.create().uv(1136, 963).cuboid(-8.0F, 0.0F, 0.0F, 16.0F, 12.0F, 25.0F, new Dilation(0.0F)), ModelTransform.of(64.0F, -123.0F, 11.0F, -1.0123F, 0.0F, 0.0F));

		ModelPartData cube_r44 = left_leg.addChild("cube_r44", ModelPartBuilder.create().uv(860, 1117).cuboid(-8.0F, 0.0F, -25.0F, 16.0F, 12.0F, 25.0F, new Dilation(0.0F)), ModelTransform.of(64.0F, -123.0F, -11.0F, 1.0123F, 0.0F, 0.0F));

		ModelPartData right_leg = legs.addChild("right_leg", ModelPartBuilder.create().uv(960, 699).cuboid(-72.0F, -102.0F, -24.0F, 16.0F, 102.0F, 48.0F, new Dilation(0.0F))
		.uv(1080, 1137).cuboid(-72.0F, -123.0F, -11.0F, 16.0F, 21.0F, 22.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

		ModelPartData cube_r45 = right_leg.addChild("cube_r45", ModelPartBuilder.create().uv(1146, 849).cuboid(-8.0F, 0.0F, 0.0F, 16.0F, 12.0F, 25.0F, new Dilation(0.0F)), ModelTransform.of(-64.0F, -123.0F, 11.0F, -1.0123F, 0.0F, 0.0F));

		ModelPartData cube_r46 = right_leg.addChild("cube_r46", ModelPartBuilder.create().uv(0, 1145).cuboid(-8.0F, 0.0F, -25.0F, 16.0F, 12.0F, 25.0F, new Dilation(0.0F)), ModelTransform.of(-64.0F, -123.0F, -11.0F, 1.0123F, 0.0F, 0.0F));
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