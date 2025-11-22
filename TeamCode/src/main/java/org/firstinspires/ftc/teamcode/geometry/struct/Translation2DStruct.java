// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package org.firstinspires.ftc.teamcode.geometry.struct;

import org.firstinspires.ftc.teamcode.geometry.Vector2D;
import org.psilynx.psikit.core.wpi.Struct;

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
    return new Vector2D(y * 39.37, -x * 39.37);
  }

  @Override
  public void pack(ByteBuffer bb, Vector2D value) {
    bb.putDouble(- value.getY() / 39.37);
    bb.putDouble(value.getX() / 39.37);
  }

  @Override
  public boolean isImmutable() {
    return true;
  }
}
