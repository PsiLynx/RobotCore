// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package org.firstinspires.ftc.teamcode.geometry.struct;

import org.firstinspires.ftc.teamcode.geometry.Vector3D;
import org.psilynx.psikit.core.wpi.Struct;

import java.nio.ByteBuffer;

public class Translation3DStruct implements Struct<Vector3D> {
  @Override
  public Class<Vector3D> getTypeClass() {
    return Vector3D.class;
  }

  @Override
  public String getTypeName() {
    return "Translation3d";
  }

  @Override
  public int getSize() {
    return kSizeDouble * 3;
  }

  @Override
  public String getSchema() {
    return "double x;double y;double z";
  }

  @Override
  public Vector3D unpack(ByteBuffer bb) {
    double x = bb.getDouble();
    double y = bb.getDouble();
    double z = bb.getDouble();
    return new Vector3D(x, y, z);
  }

  @Override
  public void pack(ByteBuffer bb, Vector3D value) {
    bb.putDouble(value.getX());
    bb.putDouble(value.getY());
    bb.putDouble(value.getZ());
  }

  @Override
  public boolean isImmutable() {
    return true;
  }
}
