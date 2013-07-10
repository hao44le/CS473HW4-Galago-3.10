/*******************************************************************************
 * Copyright 2012 Edgar Meij
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
/**
 * Autogenerated by Thrift Compiler (0.8.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package org.lemurproject.galago.contrib.parse.kba;

import java.util.BitSet;
import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import org.apache.thrift.protocol.TTupleProtocol;
import org.apache.thrift.scheme.IScheme;
import org.apache.thrift.scheme.SchemeFactory;
import org.apache.thrift.scheme.StandardScheme;
import org.apache.thrift.scheme.TupleScheme;

/**
 * StreamTime is a timestamp measured in seconds since the 1970 epoch.
 * 'news', 'linking', and 'social' each have slightly different ways
 * of generating these timestamps.  See details:
 * http://trec-kba.org/kba-stream-corpus-2012.shtml
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public class StreamTime implements
    org.apache.thrift.TBase<StreamTime, StreamTime._Fields>,
    java.io.Serializable, Cloneable {
  /**
   * 
   */
  private static final long serialVersionUID = 561578017751838669L;

  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct(
      "StreamTime");

  private static final org.apache.thrift.protocol.TField EPOCH_TICKS_FIELD_DESC = new org.apache.thrift.protocol.TField(
      "epoch_ticks", org.apache.thrift.protocol.TType.DOUBLE, (short) 1);
  private static final org.apache.thrift.protocol.TField ZULU_TIMESTAMP_FIELD_DESC = new org.apache.thrift.protocol.TField(
      "zulu_timestamp", org.apache.thrift.protocol.TType.STRING, (short) 2);

  private static final Map<Class<? extends IScheme>, SchemeFactory> schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>();
  static {
    schemes.put(StandardScheme.class, new StreamTimeStandardSchemeFactory());
    schemes.put(TupleScheme.class, new StreamTimeTupleSchemeFactory());
  }

  public double epoch_ticks; // required
  public String zulu_timestamp; // required

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    EPOCH_TICKS((short) 1, "epoch_ticks"), ZULU_TIMESTAMP((short) 2,
        "zulu_timestamp");

    private static final Map<String, _Fields> byName = new HashMap<String, _Fields>();

    static {
      for (_Fields field : EnumSet.allOf(_Fields.class)) {
        byName.put(field.getFieldName(), field);
      }
    }

    /**
     * Find the _Fields constant that matches fieldId, or null if its not found.
     */
    public static _Fields findByThriftId(int fieldId) {
      switch (fieldId) {
        case 1: // EPOCH_TICKS
          return EPOCH_TICKS;
        case 2: // ZULU_TIMESTAMP
          return ZULU_TIMESTAMP;
        default:
          return null;
      }
    }

    /**
     * Find the _Fields constant that matches fieldId, throwing an exception
     * if it is not found.
     */
    public static _Fields findByThriftIdOrThrow(int fieldId) {
      _Fields fields = findByThriftId(fieldId);
      if (fields == null)
        throw new IllegalArgumentException("Field " + fieldId
            + " doesn't exist!");
      return fields;
    }

    /**
     * Find the _Fields constant that matches name, or null if its not found.
     */
    public static _Fields findByName(String name) {
      return byName.get(name);
    }

    private final short _thriftId;
    private final String _fieldName;

    _Fields(short thriftId, String fieldName) {
      _thriftId = thriftId;
      _fieldName = fieldName;
    }

    public short getThriftFieldId() {
      return _thriftId;
    }

    public String getFieldName() {
      return _fieldName;
    }
  }

  // isset id assignments
  private static final int __EPOCH_TICKS_ISSET_ID = 0;
  private BitSet __isset_bit_vector = new BitSet(1);
  public static final Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
  static {
    Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(
        _Fields.class);
    tmpMap.put(_Fields.EPOCH_TICKS,
        new org.apache.thrift.meta_data.FieldMetaData("epoch_ticks",
            org.apache.thrift.TFieldRequirementType.DEFAULT,
            new org.apache.thrift.meta_data.FieldValueMetaData(
                org.apache.thrift.protocol.TType.DOUBLE)));
    tmpMap.put(_Fields.ZULU_TIMESTAMP,
        new org.apache.thrift.meta_data.FieldMetaData("zulu_timestamp",
            org.apache.thrift.TFieldRequirementType.DEFAULT,
            new org.apache.thrift.meta_data.FieldValueMetaData(
                org.apache.thrift.protocol.TType.STRING)));
    metaDataMap = Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(
        StreamTime.class, metaDataMap);
  }

  public StreamTime() {
  }

  public StreamTime(double epoch_ticks, String zulu_timestamp) {
    this();
    this.epoch_ticks = epoch_ticks;
    setEpoch_ticksIsSet(true);
    this.zulu_timestamp = zulu_timestamp;
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public StreamTime(StreamTime other) {
    __isset_bit_vector.clear();
    __isset_bit_vector.or(other.__isset_bit_vector);
    this.epoch_ticks = other.epoch_ticks;
    if (other.isSetZulu_timestamp()) {
      this.zulu_timestamp = other.zulu_timestamp;
    }
  }

  public StreamTime deepCopy() {
    return new StreamTime(this);
  }

  @Override
  public void clear() {
    setEpoch_ticksIsSet(false);
    this.epoch_ticks = 0.0;
    this.zulu_timestamp = null;
  }

  public double getEpoch_ticks() {
    return this.epoch_ticks;
  }

  public StreamTime setEpoch_ticks(double epoch_ticks) {
    this.epoch_ticks = epoch_ticks;
    setEpoch_ticksIsSet(true);
    return this;
  }

  public void unsetEpoch_ticks() {
    __isset_bit_vector.clear(__EPOCH_TICKS_ISSET_ID);
  }

  /** Returns true if field epoch_ticks is set (has been assigned a value) and false otherwise */
  public boolean isSetEpoch_ticks() {
    return __isset_bit_vector.get(__EPOCH_TICKS_ISSET_ID);
  }

  public void setEpoch_ticksIsSet(boolean value) {
    __isset_bit_vector.set(__EPOCH_TICKS_ISSET_ID, value);
  }

  public String getZulu_timestamp() {
    return this.zulu_timestamp;
  }

  public StreamTime setZulu_timestamp(String zulu_timestamp) {
    this.zulu_timestamp = zulu_timestamp;
    return this;
  }

  public void unsetZulu_timestamp() {
    this.zulu_timestamp = null;
  }

  /** Returns true if field zulu_timestamp is set (has been assigned a value) and false otherwise */
  public boolean isSetZulu_timestamp() {
    return this.zulu_timestamp != null;
  }

  public void setZulu_timestampIsSet(boolean value) {
    if (!value) {
      this.zulu_timestamp = null;
    }
  }

  public void setFieldValue(_Fields field, Object value) {
    switch (field) {
      case EPOCH_TICKS:
        if (value == null) {
          unsetEpoch_ticks();
        } else {
          setEpoch_ticks((Double) value);
        }
        break;

      case ZULU_TIMESTAMP:
        if (value == null) {
          unsetZulu_timestamp();
        } else {
          setZulu_timestamp((String) value);
        }
        break;

    }
  }

  public Object getFieldValue(_Fields field) {
    switch (field) {
      case EPOCH_TICKS:
        return Double.valueOf(getEpoch_ticks());

      case ZULU_TIMESTAMP:
        return getZulu_timestamp();

    }
    throw new IllegalStateException();
  }

  /** Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise */
  public boolean isSet(_Fields field) {
    if (field == null) {
      throw new IllegalArgumentException();
    }

    switch (field) {
      case EPOCH_TICKS:
        return isSetEpoch_ticks();
      case ZULU_TIMESTAMP:
        return isSetZulu_timestamp();
    }
    throw new IllegalStateException();
  }

  @Override
  public boolean equals(Object that) {
    if (that == null)
      return false;
    if (that instanceof StreamTime)
      return this.equals((StreamTime) that);
    return false;
  }

  public boolean equals(StreamTime that) {
    if (that == null)
      return false;

    boolean this_present_epoch_ticks = true;
    boolean that_present_epoch_ticks = true;
    if (this_present_epoch_ticks || that_present_epoch_ticks) {
      if (!(this_present_epoch_ticks && that_present_epoch_ticks))
        return false;
      if (this.epoch_ticks != that.epoch_ticks)
        return false;
    }

    boolean this_present_zulu_timestamp = true && this.isSetZulu_timestamp();
    boolean that_present_zulu_timestamp = true && that.isSetZulu_timestamp();
    if (this_present_zulu_timestamp || that_present_zulu_timestamp) {
      if (!(this_present_zulu_timestamp && that_present_zulu_timestamp))
        return false;
      if (!this.zulu_timestamp.equals(that.zulu_timestamp))
        return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    return 0;
  }

  public int compareTo(StreamTime other) {
    if (!getClass().equals(other.getClass())) {
      return getClass().getName().compareTo(other.getClass().getName());
    }

    int lastComparison = 0;
    StreamTime typedOther = (StreamTime) other;

    lastComparison = Boolean.valueOf(isSetEpoch_ticks()).compareTo(
        typedOther.isSetEpoch_ticks());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetEpoch_ticks()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(
          this.epoch_ticks, typedOther.epoch_ticks);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetZulu_timestamp()).compareTo(
        typedOther.isSetZulu_timestamp());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetZulu_timestamp()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(
          this.zulu_timestamp, typedOther.zulu_timestamp);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    return 0;
  }

  public _Fields fieldForId(int fieldId) {
    return _Fields.findByThriftId(fieldId);
  }

  public void read(org.apache.thrift.protocol.TProtocol iprot)
      throws org.apache.thrift.TException {
    schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
  }

  public void write(org.apache.thrift.protocol.TProtocol oprot)
      throws org.apache.thrift.TException {
    schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder("StreamTime(");
    boolean first = true;

    sb.append("epoch_ticks:");
    sb.append(this.epoch_ticks);
    first = false;
    if (!first)
      sb.append(", ");
    sb.append("zulu_timestamp:");
    if (this.zulu_timestamp == null) {
      sb.append("null");
    } else {
      sb.append(this.zulu_timestamp);
    }
    first = false;
    sb.append(")");
    return sb.toString();
  }

  public void validate() throws org.apache.thrift.TException {
    // check for required fields
  }

  private void writeObject(java.io.ObjectOutputStream out)
      throws java.io.IOException {
    try {
      write(new org.apache.thrift.protocol.TCompactProtocol(
          new org.apache.thrift.transport.TIOStreamTransport(out)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

  private void readObject(java.io.ObjectInputStream in)
      throws java.io.IOException, ClassNotFoundException {
    try {
      // it doesn't seem like you should have to do this, but java serialization
      // is wacky, and doesn't call the default constructor.
      __isset_bit_vector = new BitSet(1);
      read(new org.apache.thrift.protocol.TCompactProtocol(
          new org.apache.thrift.transport.TIOStreamTransport(in)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

  private static class StreamTimeStandardSchemeFactory implements SchemeFactory {
    public StreamTimeStandardScheme getScheme() {
      return new StreamTimeStandardScheme();
    }
  }

  private static class StreamTimeStandardScheme extends
      StandardScheme<StreamTime> {

    public void read(org.apache.thrift.protocol.TProtocol iprot,
        StreamTime struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TField schemeField;
      iprot.readStructBegin();
      while (true) {
        schemeField = iprot.readFieldBegin();
        if (schemeField.type == org.apache.thrift.protocol.TType.STOP) {
          break;
        }
        switch (schemeField.id) {
          case 1: // EPOCH_TICKS
            if (schemeField.type == org.apache.thrift.protocol.TType.DOUBLE) {
              struct.epoch_ticks = iprot.readDouble();
              struct.setEpoch_ticksIsSet(true);
            } else {
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot,
                  schemeField.type);
            }
            break;
          case 2: // ZULU_TIMESTAMP
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct.zulu_timestamp = iprot.readString();
              struct.setZulu_timestampIsSet(true);
            } else {
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot,
                  schemeField.type);
            }
            break;
          default:
            org.apache.thrift.protocol.TProtocolUtil.skip(iprot,
                schemeField.type);
        }
        iprot.readFieldEnd();
      }
      iprot.readStructEnd();

      // check for required fields of primitive type, which can't be checked in
      // the validate method
      struct.validate();
    }

    public void write(org.apache.thrift.protocol.TProtocol oprot,
        StreamTime struct) throws org.apache.thrift.TException {
      struct.validate();

      oprot.writeStructBegin(STRUCT_DESC);
      oprot.writeFieldBegin(EPOCH_TICKS_FIELD_DESC);
      oprot.writeDouble(struct.epoch_ticks);
      oprot.writeFieldEnd();
      if (struct.zulu_timestamp != null) {
        oprot.writeFieldBegin(ZULU_TIMESTAMP_FIELD_DESC);
        oprot.writeString(struct.zulu_timestamp);
        oprot.writeFieldEnd();
      }
      oprot.writeFieldStop();
      oprot.writeStructEnd();
    }

  }

  private static class StreamTimeTupleSchemeFactory implements SchemeFactory {

    public StreamTimeTupleScheme getScheme() {
      return new StreamTimeTupleScheme();
    }
  }

  private static class StreamTimeTupleScheme extends TupleScheme<StreamTime> {

    @Override
    public void write(org.apache.thrift.protocol.TProtocol prot,
        StreamTime struct) throws org.apache.thrift.TException {
      TTupleProtocol oprot = (TTupleProtocol) prot;
      BitSet optionals = new BitSet();
      if (struct.isSetEpoch_ticks()) {
        optionals.set(0);
      }
      if (struct.isSetZulu_timestamp()) {
        optionals.set(1);
      }
      oprot.writeBitSet(optionals, 2);
      if (struct.isSetEpoch_ticks()) {
        oprot.writeDouble(struct.epoch_ticks);
      }
      if (struct.isSetZulu_timestamp()) {
        oprot.writeString(struct.zulu_timestamp);
      }
    }

    @Override
    public void read(org.apache.thrift.protocol.TProtocol prot,
        StreamTime struct) throws org.apache.thrift.TException {
      TTupleProtocol iprot = (TTupleProtocol) prot;
      BitSet incoming = iprot.readBitSet(2);
      if (incoming.get(0)) {
        struct.epoch_ticks = iprot.readDouble();
        struct.setEpoch_ticksIsSet(true);
      }
      if (incoming.get(1)) {
        struct.zulu_timestamp = iprot.readString();
        struct.setZulu_timestampIsSet(true);
      }
    }
  }

}
