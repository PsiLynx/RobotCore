// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package org.firstinspires.ftc.teamcode.util.geometry.struct;

import org.firstinspires.ftc.teamcode.util.geometry.Pose2D;
import org.firstinspires.ftc.teamcode.util.geometry.Rotation2D;
import org.firstinspires.ftc.teamcode.util.geometry.Vector2D;
import org.firstinspires.ftc.teamcode.akit.wpi.Struct;

import java.nio.ByteBuffer;

public class Pose2DStruct implements Struct<Pose2D> {
  @Override
  public Class<Pose2D> getTypeClass() {
    return Pose2D.class;
  }

  @Override
  public String getTypeName() {
    return "Pose2d";
  }

  @Override
  public int getSize() {
    return Vector2D.Companion.getStruct().getSize() + Rotation2D.Companion.getStruct().getSize();
  }

  @Override
  public String getSchema() {
    return "Translation2d translation;Rotation2d rotation";
  }

  @Override
  public Struct<?>[] getNested() {
    return new Struct<?>[] {Vector2D.Companion.getStruct(), Rotation2D.Companion.getStruct()};
  }

  @Override
  public Pose2D unpack(ByteBuffer bb) {
    Vector2D translation = Vector2D.Companion.getStruct().unpack(bb);
    Rotation2D rotation = Rotation2D.Companion.getStruct().unpack(bb);
    return new Pose2D(translation, rotation);
  }

  @Override
  public void pack(ByteBuffer bb, Pose2D value) {
    Vector2D.Companion.getStruct().pack(bb, value.getVector());
    Rotation2D.Companion.getStruct().pack(bb, value.getHeading());
  }

  @Override
  public boolean isImmutable() {
    return true;
  }
}
