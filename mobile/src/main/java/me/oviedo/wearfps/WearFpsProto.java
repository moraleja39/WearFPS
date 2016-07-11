// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: wear_fps_proto.proto

package me.oviedo.wearfps;

public final class WearFpsProto {
  private WearFpsProto() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
  }
  public interface DataIntOrBuilder extends
      // @@protoc_insertion_point(interface_extends:DataInt)
      com.google.protobuf.MessageOrBuilder {

    /**
     * <code>optional int32 cpuLoad = 1;</code>
     */
    int getCpuLoad();

    /**
     * <code>optional int32 gpuLoad = 2;</code>
     */
    int getGpuLoad();

    /**
     * <code>optional int32 fps = 3;</code>
     */
    int getFps();

    /**
     * <code>optional int32 cpuTemp = 4;</code>
     */
    int getCpuTemp();

    /**
     * <code>optional int32 gpuTemp = 5;</code>
     */
    int getGpuTemp();

    /**
     * <code>optional int32 cpuFreq = 6;</code>
     */
    int getCpuFreq();

    /**
     * <code>optional int32 gpuFreq = 7;</code>
     */
    int getGpuFreq();
  }
  /**
   * Protobuf type {@code DataInt}
   */
  public  static final class DataInt extends
      com.google.protobuf.GeneratedMessage implements
      // @@protoc_insertion_point(message_implements:DataInt)
      DataIntOrBuilder {
    // Use DataInt.newBuilder() to construct.
    private DataInt(com.google.protobuf.GeneratedMessage.Builder<?> builder) {
      super(builder);
    }
    private DataInt() {
      cpuLoad_ = 0;
      gpuLoad_ = 0;
      fps_ = 0;
      cpuTemp_ = 0;
      gpuTemp_ = 0;
      cpuFreq_ = 0;
      gpuFreq_ = 0;
    }

    @Override
    public final com.google.protobuf.UnknownFieldSet
    getUnknownFields() {
      return com.google.protobuf.UnknownFieldSet.getDefaultInstance();
    }
    private DataInt(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      this();
      int mutable_bitField0_ = 0;
      try {
        boolean done = false;
        while (!done) {
          int tag = input.readTag();
          switch (tag) {
            case 0:
              done = true;
              break;
            default: {
              if (!input.skipField(tag)) {
                done = true;
              }
              break;
            }
            case 8: {

              cpuLoad_ = input.readInt32();
              break;
            }
            case 16: {

              gpuLoad_ = input.readInt32();
              break;
            }
            case 24: {

              fps_ = input.readInt32();
              break;
            }
            case 32: {

              cpuTemp_ = input.readInt32();
              break;
            }
            case 40: {

              gpuTemp_ = input.readInt32();
              break;
            }
            case 48: {

              cpuFreq_ = input.readInt32();
              break;
            }
            case 56: {

              gpuFreq_ = input.readInt32();
              break;
            }
          }
        }
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        throw e.setUnfinishedMessage(this);
      } catch (java.io.IOException e) {
        throw new com.google.protobuf.InvalidProtocolBufferException(
            e).setUnfinishedMessage(this);
      } finally {
        makeExtensionsImmutable();
      }
    }
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return WearFpsProto.internal_static_DataInt_descriptor;
    }

    protected FieldAccessorTable
        internalGetFieldAccessorTable() {
      return WearFpsProto.internal_static_DataInt_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              DataInt.class, Builder.class);
    }

    public static final int CPULOAD_FIELD_NUMBER = 1;
    private int cpuLoad_;
    /**
     * <code>optional int32 cpuLoad = 1;</code>
     */
    public int getCpuLoad() {
      return cpuLoad_;
    }

    public static final int GPULOAD_FIELD_NUMBER = 2;
    private int gpuLoad_;
    /**
     * <code>optional int32 gpuLoad = 2;</code>
     */
    public int getGpuLoad() {
      return gpuLoad_;
    }

    public static final int FPS_FIELD_NUMBER = 3;
    private int fps_;
    /**
     * <code>optional int32 fps = 3;</code>
     */
    public int getFps() {
      return fps_;
    }

    public static final int CPUTEMP_FIELD_NUMBER = 4;
    private int cpuTemp_;
    /**
     * <code>optional int32 cpuTemp = 4;</code>
     */
    public int getCpuTemp() {
      return cpuTemp_;
    }

    public static final int GPUTEMP_FIELD_NUMBER = 5;
    private int gpuTemp_;
    /**
     * <code>optional int32 gpuTemp = 5;</code>
     */
    public int getGpuTemp() {
      return gpuTemp_;
    }

    public static final int CPUFREQ_FIELD_NUMBER = 6;
    private int cpuFreq_;
    /**
     * <code>optional int32 cpuFreq = 6;</code>
     */
    public int getCpuFreq() {
      return cpuFreq_;
    }

    public static final int GPUFREQ_FIELD_NUMBER = 7;
    private int gpuFreq_;
    /**
     * <code>optional int32 gpuFreq = 7;</code>
     */
    public int getGpuFreq() {
      return gpuFreq_;
    }

    private byte memoizedIsInitialized = -1;
    public final boolean isInitialized() {
      byte isInitialized = memoizedIsInitialized;
      if (isInitialized == 1) return true;
      if (isInitialized == 0) return false;

      memoizedIsInitialized = 1;
      return true;
    }

    public void writeTo(com.google.protobuf.CodedOutputStream output)
                        throws java.io.IOException {
      if (cpuLoad_ != 0) {
        output.writeInt32(1, cpuLoad_);
      }
      if (gpuLoad_ != 0) {
        output.writeInt32(2, gpuLoad_);
      }
      if (fps_ != 0) {
        output.writeInt32(3, fps_);
      }
      if (cpuTemp_ != 0) {
        output.writeInt32(4, cpuTemp_);
      }
      if (gpuTemp_ != 0) {
        output.writeInt32(5, gpuTemp_);
      }
      if (cpuFreq_ != 0) {
        output.writeInt32(6, cpuFreq_);
      }
      if (gpuFreq_ != 0) {
        output.writeInt32(7, gpuFreq_);
      }
    }

    public int getSerializedSize() {
      int size = memoizedSize;
      if (size != -1) return size;

      size = 0;
      if (cpuLoad_ != 0) {
        size += com.google.protobuf.CodedOutputStream
          .computeInt32Size(1, cpuLoad_);
      }
      if (gpuLoad_ != 0) {
        size += com.google.protobuf.CodedOutputStream
          .computeInt32Size(2, gpuLoad_);
      }
      if (fps_ != 0) {
        size += com.google.protobuf.CodedOutputStream
          .computeInt32Size(3, fps_);
      }
      if (cpuTemp_ != 0) {
        size += com.google.protobuf.CodedOutputStream
          .computeInt32Size(4, cpuTemp_);
      }
      if (gpuTemp_ != 0) {
        size += com.google.protobuf.CodedOutputStream
          .computeInt32Size(5, gpuTemp_);
      }
      if (cpuFreq_ != 0) {
        size += com.google.protobuf.CodedOutputStream
          .computeInt32Size(6, cpuFreq_);
      }
      if (gpuFreq_ != 0) {
        size += com.google.protobuf.CodedOutputStream
          .computeInt32Size(7, gpuFreq_);
      }
      memoizedSize = size;
      return size;
    }

    private static final long serialVersionUID = 0L;
    public static DataInt parseFrom(
        com.google.protobuf.ByteString data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static DataInt parseFrom(
        com.google.protobuf.ByteString data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static DataInt parseFrom(byte[] data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static DataInt parseFrom(
        byte[] data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static DataInt parseFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessage
          .parseWithIOException(PARSER, input);
    }
    public static DataInt parseFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessage
          .parseWithIOException(PARSER, input, extensionRegistry);
    }
    public static DataInt parseDelimitedFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessage
          .parseDelimitedWithIOException(PARSER, input);
    }
    public static DataInt parseDelimitedFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessage
          .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }
    public static DataInt parseFrom(
        com.google.protobuf.CodedInputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessage
          .parseWithIOException(PARSER, input);
    }
    public static DataInt parseFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessage
          .parseWithIOException(PARSER, input, extensionRegistry);
    }

    public Builder newBuilderForType() { return newBuilder(); }
    public static Builder newBuilder() {
      return DEFAULT_INSTANCE.toBuilder();
    }
    public static Builder newBuilder(DataInt prototype) {
      return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
    }
    public Builder toBuilder() {
      return this == DEFAULT_INSTANCE
          ? new Builder() : new Builder().mergeFrom(this);
    }

    @Override
    protected Builder newBuilderForType(
        BuilderParent parent) {
      Builder builder = new Builder(parent);
      return builder;
    }
    /**
     * Protobuf type {@code DataInt}
     */
    public static final class Builder extends
        com.google.protobuf.GeneratedMessage.Builder<Builder> implements
        // @@protoc_insertion_point(builder_implements:DataInt)
        DataIntOrBuilder {
      public static final com.google.protobuf.Descriptors.Descriptor
          getDescriptor() {
        return WearFpsProto.internal_static_DataInt_descriptor;
      }

      protected FieldAccessorTable
          internalGetFieldAccessorTable() {
        return WearFpsProto.internal_static_DataInt_fieldAccessorTable
            .ensureFieldAccessorsInitialized(
                DataInt.class, Builder.class);
      }

      // Construct using me.oviedo.wearfps.WearFpsProto.DataInt.newBuilder()
      private Builder() {
        maybeForceBuilderInitialization();
      }

      private Builder(
          BuilderParent parent) {
        super(parent);
        maybeForceBuilderInitialization();
      }
      private void maybeForceBuilderInitialization() {
        if (com.google.protobuf.GeneratedMessage.alwaysUseFieldBuilders) {
        }
      }
      public Builder clear() {
        super.clear();
        cpuLoad_ = 0;

        gpuLoad_ = 0;

        fps_ = 0;

        cpuTemp_ = 0;

        gpuTemp_ = 0;

        cpuFreq_ = 0;

        gpuFreq_ = 0;

        return this;
      }

      public com.google.protobuf.Descriptors.Descriptor
          getDescriptorForType() {
        return WearFpsProto.internal_static_DataInt_descriptor;
      }

      public DataInt getDefaultInstanceForType() {
        return DataInt.getDefaultInstance();
      }

      public DataInt build() {
        DataInt result = buildPartial();
        if (!result.isInitialized()) {
          throw newUninitializedMessageException(result);
        }
        return result;
      }

      public DataInt buildPartial() {
        DataInt result = new DataInt(this);
        result.cpuLoad_ = cpuLoad_;
        result.gpuLoad_ = gpuLoad_;
        result.fps_ = fps_;
        result.cpuTemp_ = cpuTemp_;
        result.gpuTemp_ = gpuTemp_;
        result.cpuFreq_ = cpuFreq_;
        result.gpuFreq_ = gpuFreq_;
        onBuilt();
        return result;
      }

      public Builder mergeFrom(com.google.protobuf.Message other) {
        if (other instanceof DataInt) {
          return mergeFrom((DataInt)other);
        } else {
          super.mergeFrom(other);
          return this;
        }
      }

      public Builder mergeFrom(DataInt other) {
        if (other == DataInt.getDefaultInstance()) return this;
        if (other.getCpuLoad() != 0) {
          setCpuLoad(other.getCpuLoad());
        }
        if (other.getGpuLoad() != 0) {
          setGpuLoad(other.getGpuLoad());
        }
        if (other.getFps() != 0) {
          setFps(other.getFps());
        }
        if (other.getCpuTemp() != 0) {
          setCpuTemp(other.getCpuTemp());
        }
        if (other.getGpuTemp() != 0) {
          setGpuTemp(other.getGpuTemp());
        }
        if (other.getCpuFreq() != 0) {
          setCpuFreq(other.getCpuFreq());
        }
        if (other.getGpuFreq() != 0) {
          setGpuFreq(other.getGpuFreq());
        }
        onChanged();
        return this;
      }

      public final boolean isInitialized() {
        return true;
      }

      public Builder mergeFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws java.io.IOException {
        DataInt parsedMessage = null;
        try {
          parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
        } catch (com.google.protobuf.InvalidProtocolBufferException e) {
          parsedMessage = (DataInt) e.getUnfinishedMessage();
          throw e.unwrapIOException();
        } finally {
          if (parsedMessage != null) {
            mergeFrom(parsedMessage);
          }
        }
        return this;
      }

      private int cpuLoad_ ;
      /**
       * <code>optional int32 cpuLoad = 1;</code>
       */
      public int getCpuLoad() {
        return cpuLoad_;
      }
      /**
       * <code>optional int32 cpuLoad = 1;</code>
       */
      public Builder setCpuLoad(int value) {
        
        cpuLoad_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>optional int32 cpuLoad = 1;</code>
       */
      public Builder clearCpuLoad() {
        
        cpuLoad_ = 0;
        onChanged();
        return this;
      }

      private int gpuLoad_ ;
      /**
       * <code>optional int32 gpuLoad = 2;</code>
       */
      public int getGpuLoad() {
        return gpuLoad_;
      }
      /**
       * <code>optional int32 gpuLoad = 2;</code>
       */
      public Builder setGpuLoad(int value) {
        
        gpuLoad_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>optional int32 gpuLoad = 2;</code>
       */
      public Builder clearGpuLoad() {
        
        gpuLoad_ = 0;
        onChanged();
        return this;
      }

      private int fps_ ;
      /**
       * <code>optional int32 fps = 3;</code>
       */
      public int getFps() {
        return fps_;
      }
      /**
       * <code>optional int32 fps = 3;</code>
       */
      public Builder setFps(int value) {
        
        fps_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>optional int32 fps = 3;</code>
       */
      public Builder clearFps() {
        
        fps_ = 0;
        onChanged();
        return this;
      }

      private int cpuTemp_ ;
      /**
       * <code>optional int32 cpuTemp = 4;</code>
       */
      public int getCpuTemp() {
        return cpuTemp_;
      }
      /**
       * <code>optional int32 cpuTemp = 4;</code>
       */
      public Builder setCpuTemp(int value) {
        
        cpuTemp_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>optional int32 cpuTemp = 4;</code>
       */
      public Builder clearCpuTemp() {
        
        cpuTemp_ = 0;
        onChanged();
        return this;
      }

      private int gpuTemp_ ;
      /**
       * <code>optional int32 gpuTemp = 5;</code>
       */
      public int getGpuTemp() {
        return gpuTemp_;
      }
      /**
       * <code>optional int32 gpuTemp = 5;</code>
       */
      public Builder setGpuTemp(int value) {
        
        gpuTemp_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>optional int32 gpuTemp = 5;</code>
       */
      public Builder clearGpuTemp() {
        
        gpuTemp_ = 0;
        onChanged();
        return this;
      }

      private int cpuFreq_ ;
      /**
       * <code>optional int32 cpuFreq = 6;</code>
       */
      public int getCpuFreq() {
        return cpuFreq_;
      }
      /**
       * <code>optional int32 cpuFreq = 6;</code>
       */
      public Builder setCpuFreq(int value) {
        
        cpuFreq_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>optional int32 cpuFreq = 6;</code>
       */
      public Builder clearCpuFreq() {
        
        cpuFreq_ = 0;
        onChanged();
        return this;
      }

      private int gpuFreq_ ;
      /**
       * <code>optional int32 gpuFreq = 7;</code>
       */
      public int getGpuFreq() {
        return gpuFreq_;
      }
      /**
       * <code>optional int32 gpuFreq = 7;</code>
       */
      public Builder setGpuFreq(int value) {
        
        gpuFreq_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>optional int32 gpuFreq = 7;</code>
       */
      public Builder clearGpuFreq() {
        
        gpuFreq_ = 0;
        onChanged();
        return this;
      }
      public final Builder setUnknownFields(
          final com.google.protobuf.UnknownFieldSet unknownFields) {
        return this;
      }

      public final Builder mergeUnknownFields(
          final com.google.protobuf.UnknownFieldSet unknownFields) {
        return this;
      }


      // @@protoc_insertion_point(builder_scope:DataInt)
    }

    // @@protoc_insertion_point(class_scope:DataInt)
    private static final DataInt DEFAULT_INSTANCE;
    static {
      DEFAULT_INSTANCE = new DataInt();
    }

    public static DataInt getDefaultInstance() {
      return DEFAULT_INSTANCE;
    }

    private static final com.google.protobuf.Parser<DataInt>
        PARSER = new com.google.protobuf.AbstractParser<DataInt>() {
      public DataInt parsePartialFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws com.google.protobuf.InvalidProtocolBufferException {
          return new DataInt(input, extensionRegistry);
      }
    };

    public static com.google.protobuf.Parser<DataInt> parser() {
      return PARSER;
    }

    @Override
    public com.google.protobuf.Parser<DataInt> getParserForType() {
      return PARSER;
    }

    public DataInt getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
    }

  }

  public interface ComputerInfoOrBuilder extends
      // @@protoc_insertion_point(interface_extends:ComputerInfo)
      com.google.protobuf.MessageOrBuilder {

    /**
     * <code>optional string cpuName = 1;</code>
     */
    String getCpuName();
    /**
     * <code>optional string cpuName = 1;</code>
     */
    com.google.protobuf.ByteString
        getCpuNameBytes();

    /**
     * <code>optional string gpuName = 2;</code>
     */
    String getGpuName();
    /**
     * <code>optional string gpuName = 2;</code>
     */
    com.google.protobuf.ByteString
        getGpuNameBytes();
  }
  /**
   * Protobuf type {@code ComputerInfo}
   */
  public  static final class ComputerInfo extends
      com.google.protobuf.GeneratedMessage implements
      // @@protoc_insertion_point(message_implements:ComputerInfo)
      ComputerInfoOrBuilder {
    // Use ComputerInfo.newBuilder() to construct.
    private ComputerInfo(com.google.protobuf.GeneratedMessage.Builder<?> builder) {
      super(builder);
    }
    private ComputerInfo() {
      cpuName_ = "";
      gpuName_ = "";
    }

    @Override
    public final com.google.protobuf.UnknownFieldSet
    getUnknownFields() {
      return com.google.protobuf.UnknownFieldSet.getDefaultInstance();
    }
    private ComputerInfo(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      this();
      int mutable_bitField0_ = 0;
      try {
        boolean done = false;
        while (!done) {
          int tag = input.readTag();
          switch (tag) {
            case 0:
              done = true;
              break;
            default: {
              if (!input.skipField(tag)) {
                done = true;
              }
              break;
            }
            case 10: {
              String s = input.readStringRequireUtf8();

              cpuName_ = s;
              break;
            }
            case 18: {
              String s = input.readStringRequireUtf8();

              gpuName_ = s;
              break;
            }
          }
        }
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        throw e.setUnfinishedMessage(this);
      } catch (java.io.IOException e) {
        throw new com.google.protobuf.InvalidProtocolBufferException(
            e).setUnfinishedMessage(this);
      } finally {
        makeExtensionsImmutable();
      }
    }
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return WearFpsProto.internal_static_ComputerInfo_descriptor;
    }

    protected FieldAccessorTable
        internalGetFieldAccessorTable() {
      return WearFpsProto.internal_static_ComputerInfo_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              ComputerInfo.class, Builder.class);
    }

    public static final int CPUNAME_FIELD_NUMBER = 1;
    private volatile Object cpuName_;
    /**
     * <code>optional string cpuName = 1;</code>
     */
    public String getCpuName() {
      Object ref = cpuName_;
      if (ref instanceof String) {
        return (String) ref;
      } else {
        com.google.protobuf.ByteString bs = 
            (com.google.protobuf.ByteString) ref;
        String s = bs.toStringUtf8();
        cpuName_ = s;
        return s;
      }
    }
    /**
     * <code>optional string cpuName = 1;</code>
     */
    public com.google.protobuf.ByteString
        getCpuNameBytes() {
      Object ref = cpuName_;
      if (ref instanceof String) {
        com.google.protobuf.ByteString b = 
            com.google.protobuf.ByteString.copyFromUtf8(
                (String) ref);
        cpuName_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }

    public static final int GPUNAME_FIELD_NUMBER = 2;
    private volatile Object gpuName_;
    /**
     * <code>optional string gpuName = 2;</code>
     */
    public String getGpuName() {
      Object ref = gpuName_;
      if (ref instanceof String) {
        return (String) ref;
      } else {
        com.google.protobuf.ByteString bs = 
            (com.google.protobuf.ByteString) ref;
        String s = bs.toStringUtf8();
        gpuName_ = s;
        return s;
      }
    }
    /**
     * <code>optional string gpuName = 2;</code>
     */
    public com.google.protobuf.ByteString
        getGpuNameBytes() {
      Object ref = gpuName_;
      if (ref instanceof String) {
        com.google.protobuf.ByteString b = 
            com.google.protobuf.ByteString.copyFromUtf8(
                (String) ref);
        gpuName_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }

    private byte memoizedIsInitialized = -1;
    public final boolean isInitialized() {
      byte isInitialized = memoizedIsInitialized;
      if (isInitialized == 1) return true;
      if (isInitialized == 0) return false;

      memoizedIsInitialized = 1;
      return true;
    }

    public void writeTo(com.google.protobuf.CodedOutputStream output)
                        throws java.io.IOException {
      if (!getCpuNameBytes().isEmpty()) {
        com.google.protobuf.GeneratedMessage.writeString(output, 1, cpuName_);
      }
      if (!getGpuNameBytes().isEmpty()) {
        com.google.protobuf.GeneratedMessage.writeString(output, 2, gpuName_);
      }
    }

    public int getSerializedSize() {
      int size = memoizedSize;
      if (size != -1) return size;

      size = 0;
      if (!getCpuNameBytes().isEmpty()) {
        size += com.google.protobuf.GeneratedMessage.computeStringSize(1, cpuName_);
      }
      if (!getGpuNameBytes().isEmpty()) {
        size += com.google.protobuf.GeneratedMessage.computeStringSize(2, gpuName_);
      }
      memoizedSize = size;
      return size;
    }

    private static final long serialVersionUID = 0L;
    public static ComputerInfo parseFrom(
        com.google.protobuf.ByteString data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static ComputerInfo parseFrom(
        com.google.protobuf.ByteString data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static ComputerInfo parseFrom(byte[] data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static ComputerInfo parseFrom(
        byte[] data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static ComputerInfo parseFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessage
          .parseWithIOException(PARSER, input);
    }
    public static ComputerInfo parseFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessage
          .parseWithIOException(PARSER, input, extensionRegistry);
    }
    public static ComputerInfo parseDelimitedFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessage
          .parseDelimitedWithIOException(PARSER, input);
    }
    public static ComputerInfo parseDelimitedFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessage
          .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }
    public static ComputerInfo parseFrom(
        com.google.protobuf.CodedInputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessage
          .parseWithIOException(PARSER, input);
    }
    public static ComputerInfo parseFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessage
          .parseWithIOException(PARSER, input, extensionRegistry);
    }

    public Builder newBuilderForType() { return newBuilder(); }
    public static Builder newBuilder() {
      return DEFAULT_INSTANCE.toBuilder();
    }
    public static Builder newBuilder(ComputerInfo prototype) {
      return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
    }
    public Builder toBuilder() {
      return this == DEFAULT_INSTANCE
          ? new Builder() : new Builder().mergeFrom(this);
    }

    @Override
    protected Builder newBuilderForType(
        BuilderParent parent) {
      Builder builder = new Builder(parent);
      return builder;
    }
    /**
     * Protobuf type {@code ComputerInfo}
     */
    public static final class Builder extends
        com.google.protobuf.GeneratedMessage.Builder<Builder> implements
        // @@protoc_insertion_point(builder_implements:ComputerInfo)
        ComputerInfoOrBuilder {
      public static final com.google.protobuf.Descriptors.Descriptor
          getDescriptor() {
        return WearFpsProto.internal_static_ComputerInfo_descriptor;
      }

      protected FieldAccessorTable
          internalGetFieldAccessorTable() {
        return WearFpsProto.internal_static_ComputerInfo_fieldAccessorTable
            .ensureFieldAccessorsInitialized(
                ComputerInfo.class, Builder.class);
      }

      // Construct using me.oviedo.wearfps.WearFpsProto.ComputerInfo.newBuilder()
      private Builder() {
        maybeForceBuilderInitialization();
      }

      private Builder(
          BuilderParent parent) {
        super(parent);
        maybeForceBuilderInitialization();
      }
      private void maybeForceBuilderInitialization() {
        if (com.google.protobuf.GeneratedMessage.alwaysUseFieldBuilders) {
        }
      }
      public Builder clear() {
        super.clear();
        cpuName_ = "";

        gpuName_ = "";

        return this;
      }

      public com.google.protobuf.Descriptors.Descriptor
          getDescriptorForType() {
        return WearFpsProto.internal_static_ComputerInfo_descriptor;
      }

      public ComputerInfo getDefaultInstanceForType() {
        return ComputerInfo.getDefaultInstance();
      }

      public ComputerInfo build() {
        ComputerInfo result = buildPartial();
        if (!result.isInitialized()) {
          throw newUninitializedMessageException(result);
        }
        return result;
      }

      public ComputerInfo buildPartial() {
        ComputerInfo result = new ComputerInfo(this);
        result.cpuName_ = cpuName_;
        result.gpuName_ = gpuName_;
        onBuilt();
        return result;
      }

      public Builder mergeFrom(com.google.protobuf.Message other) {
        if (other instanceof ComputerInfo) {
          return mergeFrom((ComputerInfo)other);
        } else {
          super.mergeFrom(other);
          return this;
        }
      }

      public Builder mergeFrom(ComputerInfo other) {
        if (other == ComputerInfo.getDefaultInstance()) return this;
        if (!other.getCpuName().isEmpty()) {
          cpuName_ = other.cpuName_;
          onChanged();
        }
        if (!other.getGpuName().isEmpty()) {
          gpuName_ = other.gpuName_;
          onChanged();
        }
        onChanged();
        return this;
      }

      public final boolean isInitialized() {
        return true;
      }

      public Builder mergeFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws java.io.IOException {
        ComputerInfo parsedMessage = null;
        try {
          parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
        } catch (com.google.protobuf.InvalidProtocolBufferException e) {
          parsedMessage = (ComputerInfo) e.getUnfinishedMessage();
          throw e.unwrapIOException();
        } finally {
          if (parsedMessage != null) {
            mergeFrom(parsedMessage);
          }
        }
        return this;
      }

      private Object cpuName_ = "";
      /**
       * <code>optional string cpuName = 1;</code>
       */
      public String getCpuName() {
        Object ref = cpuName_;
        if (!(ref instanceof String)) {
          com.google.protobuf.ByteString bs =
              (com.google.protobuf.ByteString) ref;
          String s = bs.toStringUtf8();
          cpuName_ = s;
          return s;
        } else {
          return (String) ref;
        }
      }
      /**
       * <code>optional string cpuName = 1;</code>
       */
      public com.google.protobuf.ByteString
          getCpuNameBytes() {
        Object ref = cpuName_;
        if (ref instanceof String) {
          com.google.protobuf.ByteString b = 
              com.google.protobuf.ByteString.copyFromUtf8(
                  (String) ref);
          cpuName_ = b;
          return b;
        } else {
          return (com.google.protobuf.ByteString) ref;
        }
      }
      /**
       * <code>optional string cpuName = 1;</code>
       */
      public Builder setCpuName(
          String value) {
        if (value == null) {
    throw new NullPointerException();
  }
  
        cpuName_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>optional string cpuName = 1;</code>
       */
      public Builder clearCpuName() {
        
        cpuName_ = getDefaultInstance().getCpuName();
        onChanged();
        return this;
      }
      /**
       * <code>optional string cpuName = 1;</code>
       */
      public Builder setCpuNameBytes(
          com.google.protobuf.ByteString value) {
        if (value == null) {
    throw new NullPointerException();
  }
  checkByteStringIsUtf8(value);
        
        cpuName_ = value;
        onChanged();
        return this;
      }

      private Object gpuName_ = "";
      /**
       * <code>optional string gpuName = 2;</code>
       */
      public String getGpuName() {
        Object ref = gpuName_;
        if (!(ref instanceof String)) {
          com.google.protobuf.ByteString bs =
              (com.google.protobuf.ByteString) ref;
          String s = bs.toStringUtf8();
          gpuName_ = s;
          return s;
        } else {
          return (String) ref;
        }
      }
      /**
       * <code>optional string gpuName = 2;</code>
       */
      public com.google.protobuf.ByteString
          getGpuNameBytes() {
        Object ref = gpuName_;
        if (ref instanceof String) {
          com.google.protobuf.ByteString b = 
              com.google.protobuf.ByteString.copyFromUtf8(
                  (String) ref);
          gpuName_ = b;
          return b;
        } else {
          return (com.google.protobuf.ByteString) ref;
        }
      }
      /**
       * <code>optional string gpuName = 2;</code>
       */
      public Builder setGpuName(
          String value) {
        if (value == null) {
    throw new NullPointerException();
  }
  
        gpuName_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>optional string gpuName = 2;</code>
       */
      public Builder clearGpuName() {
        
        gpuName_ = getDefaultInstance().getGpuName();
        onChanged();
        return this;
      }
      /**
       * <code>optional string gpuName = 2;</code>
       */
      public Builder setGpuNameBytes(
          com.google.protobuf.ByteString value) {
        if (value == null) {
    throw new NullPointerException();
  }
  checkByteStringIsUtf8(value);
        
        gpuName_ = value;
        onChanged();
        return this;
      }
      public final Builder setUnknownFields(
          final com.google.protobuf.UnknownFieldSet unknownFields) {
        return this;
      }

      public final Builder mergeUnknownFields(
          final com.google.protobuf.UnknownFieldSet unknownFields) {
        return this;
      }


      // @@protoc_insertion_point(builder_scope:ComputerInfo)
    }

    // @@protoc_insertion_point(class_scope:ComputerInfo)
    private static final ComputerInfo DEFAULT_INSTANCE;
    static {
      DEFAULT_INSTANCE = new ComputerInfo();
    }

    public static ComputerInfo getDefaultInstance() {
      return DEFAULT_INSTANCE;
    }

    private static final com.google.protobuf.Parser<ComputerInfo>
        PARSER = new com.google.protobuf.AbstractParser<ComputerInfo>() {
      public ComputerInfo parsePartialFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws com.google.protobuf.InvalidProtocolBufferException {
          return new ComputerInfo(input, extensionRegistry);
      }
    };

    public static com.google.protobuf.Parser<ComputerInfo> parser() {
      return PARSER;
    }

    @Override
    public com.google.protobuf.Parser<ComputerInfo> getParserForType() {
      return PARSER;
    }

    public ComputerInfo getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
    }

  }

  private static final com.google.protobuf.Descriptors.Descriptor
    internal_static_DataInt_descriptor;
  private static final 
    com.google.protobuf.GeneratedMessage.FieldAccessorTable
      internal_static_DataInt_fieldAccessorTable;
  private static final com.google.protobuf.Descriptors.Descriptor
    internal_static_ComputerInfo_descriptor;
  private static final 
    com.google.protobuf.GeneratedMessage.FieldAccessorTable
      internal_static_ComputerInfo_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static  com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    String[] descriptorData = {
      "\n\024wear_fps_proto.proto\"|\n\007DataInt\022\017\n\007cpu" +
      "Load\030\001 \001(\005\022\017\n\007gpuLoad\030\002 \001(\005\022\013\n\003fps\030\003 \001(\005" +
      "\022\017\n\007cpuTemp\030\004 \001(\005\022\017\n\007gpuTemp\030\005 \001(\005\022\017\n\007cp" +
      "uFreq\030\006 \001(\005\022\017\n\007gpuFreq\030\007 \001(\005\"0\n\014Computer" +
      "Info\022\017\n\007cpuName\030\001 \001(\t\022\017\n\007gpuName\030\002 \001(\tB\"" +
      "\n\021me.oviedo.wearfps\252\002\014WearFPSFormsb\006prot" +
      "o3"
    };
    com.google.protobuf.Descriptors.FileDescriptor.InternalDescriptorAssigner assigner =
        new com.google.protobuf.Descriptors.FileDescriptor.    InternalDescriptorAssigner() {
          public com.google.protobuf.ExtensionRegistry assignDescriptors(
              com.google.protobuf.Descriptors.FileDescriptor root) {
            descriptor = root;
            return null;
          }
        };
    com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
        }, assigner);
    internal_static_DataInt_descriptor =
      getDescriptor().getMessageTypes().get(0);
    internal_static_DataInt_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessage.FieldAccessorTable(
        internal_static_DataInt_descriptor,
        new String[] { "CpuLoad", "GpuLoad", "Fps", "CpuTemp", "GpuTemp", "CpuFreq", "GpuFreq", });
    internal_static_ComputerInfo_descriptor =
      getDescriptor().getMessageTypes().get(1);
    internal_static_ComputerInfo_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessage.FieldAccessorTable(
        internal_static_ComputerInfo_descriptor,
        new String[] { "CpuName", "GpuName", });
  }

  // @@protoc_insertion_point(outer_class_scope)
}
