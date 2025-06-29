// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package org.firstinspires.ftc.teamcode.util.geometry.struct;

import org.firstinspires.ftc.teamcode.util.geometry.Vector2D;
import org.psilynx.psikit.wpi.Struct;

import java.nio.ByteBuffer;

public class Translation2DStruct implements Struct<Vector2D> {
  @Override
  public Class<Vector2D> getTypeClass() {
    return Vector2D.class;
  }

  @Override
  public String getTypeName() {
    return "Translation2d";
  }

  @Override
  public int getSize() {
    return kSizeDouble * 2;
  }

  @Override
  public String getSchema() {
    return "double x;double y";
  }

  @Override
  public Vector2D unpack(ByteBuffer bb) {
    double x = bb.getDouble();
    double y = bb.getDouble();
    return new Vector2D(x, y);
  }

  @Override
  public void pack(ByteBuffer bb, Vector2D value) {
    bb.putDouble(value.getX());
    bb.putDouble(value.getY());
  }

  @Override
  public boolean isImmutable() {
    return true;
  }
}
