// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package org.firstinspires.ftc.teamcode.util.geometry.struct;

import org.firstinspires.ftc.teamcode.util.geometry.Rotation2D;
import org.firstinspires.ftc.teamcode.akit.wpi.Struct;

import java.nio.ByteBuffer;

public class Rotation2DStruct implements Struct<Rotation2D> {
  @Override
  public Class<Rotation2D> getTypeClass() { return Rotation2D.class; }

  @Override
  public String getTypeName() {
    return "Rotation2d";
  }

  @Override
  public int getSize() {
    return kSizeDouble;
  }

  @Override
  public String getSchema() {
    return "double value";
  }

  @Override
  public Rotation2D unpack(ByteBuffer bb) {
    double value = bb.getDouble();
    return new Rotation2D(value);
  }

  @Override
  public void pack(ByteBuffer bb, Rotation2D value) {
    bb.putDouble(value.toDouble());
  }

  @Override
  public boolean isImmutable() {
    return true;
  }
}
