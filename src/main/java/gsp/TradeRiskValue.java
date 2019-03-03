/**
 * Autogenerated by Avro
 *
 * DO NOT EDIT DIRECTLY
 */
package gsp;

import org.apache.avro.specific.SpecificData;
import org.apache.avro.message.BinaryMessageEncoder;
import org.apache.avro.message.BinaryMessageDecoder;
import org.apache.avro.message.SchemaStore;

@SuppressWarnings("all")
@org.apache.avro.specific.AvroGenerated
public class TradeRiskValue extends org.apache.avro.specific.SpecificRecordBase implements org.apache.avro.specific.SpecificRecord {
  private static final long serialVersionUID = 7312572074247899071L;
  public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{\"type\":\"record\",\"name\":\"TradeRiskValue\",\"namespace\":\"gsp\",\"fields\":[{\"name\":\"RiskMeasure\",\"type\":\"string\"},{\"name\":\"Parameter\",\"type\":[\"null\",\"string\"],\"default\":null},{\"name\":\"RiskValue\",\"type\":{\"type\":\"record\",\"name\":\"RiskAmount\",\"fields\":[{\"name\":\"Currency\",\"type\":{\"type\":\"enum\",\"name\":\"Currency\",\"symbols\":[\"USD\",\"EUR\",\"GBP\",\"JPY\"]}},{\"name\":\"Amount\",\"type\":\"double\"}]}}]}");
  public static org.apache.avro.Schema getClassSchema() { return SCHEMA$; }

  private static SpecificData MODEL$ = new SpecificData();

  private static final BinaryMessageEncoder<TradeRiskValue> ENCODER =
      new BinaryMessageEncoder<TradeRiskValue>(MODEL$, SCHEMA$);

  private static final BinaryMessageDecoder<TradeRiskValue> DECODER =
      new BinaryMessageDecoder<TradeRiskValue>(MODEL$, SCHEMA$);

  /**
   * Return the BinaryMessageDecoder instance used by this class.
   */
  public static BinaryMessageDecoder<TradeRiskValue> getDecoder() {
    return DECODER;
  }

  /**
   * Create a new BinaryMessageDecoder instance for this class that uses the specified {@link SchemaStore}.
   * @param resolver a {@link SchemaStore} used to find schemas by fingerprint
   */
  public static BinaryMessageDecoder<TradeRiskValue> createDecoder(SchemaStore resolver) {
    return new BinaryMessageDecoder<TradeRiskValue>(MODEL$, SCHEMA$, resolver);
  }

  /** Serializes this TradeRiskValue to a ByteBuffer. */
  public java.nio.ByteBuffer toByteBuffer() throws java.io.IOException {
    return ENCODER.encode(this);
  }

  /** Deserializes a TradeRiskValue from a ByteBuffer. */
  public static TradeRiskValue fromByteBuffer(
      java.nio.ByteBuffer b) throws java.io.IOException {
    return DECODER.decode(b);
  }

  @Deprecated public java.lang.CharSequence RiskMeasure;
  @Deprecated public java.lang.CharSequence Parameter;
  @Deprecated public gsp.RiskAmount RiskValue;

  /**
   * Default constructor.  Note that this does not initialize fields
   * to their default values from the schema.  If that is desired then
   * one should use <code>newBuilder()</code>.
   */
  public TradeRiskValue() {}

  /**
   * All-args constructor.
   * @param RiskMeasure The new value for RiskMeasure
   * @param Parameter The new value for Parameter
   * @param RiskValue The new value for RiskValue
   */
  public TradeRiskValue(java.lang.CharSequence RiskMeasure, java.lang.CharSequence Parameter, gsp.RiskAmount RiskValue) {
    this.RiskMeasure = RiskMeasure;
    this.Parameter = Parameter;
    this.RiskValue = RiskValue;
  }

  public org.apache.avro.Schema getSchema() { return SCHEMA$; }
  // Used by DatumWriter.  Applications should not call.
  public java.lang.Object get(int field$) {
    switch (field$) {
    case 0: return RiskMeasure;
    case 1: return Parameter;
    case 2: return RiskValue;
    default: throw new org.apache.avro.AvroRuntimeException("Bad index");
    }
  }

  // Used by DatumReader.  Applications should not call.
  @SuppressWarnings(value="unchecked")
  public void put(int field$, java.lang.Object value$) {
    switch (field$) {
    case 0: RiskMeasure = (java.lang.CharSequence)value$; break;
    case 1: Parameter = (java.lang.CharSequence)value$; break;
    case 2: RiskValue = (gsp.RiskAmount)value$; break;
    default: throw new org.apache.avro.AvroRuntimeException("Bad index");
    }
  }

  /**
   * Gets the value of the 'RiskMeasure' field.
   * @return The value of the 'RiskMeasure' field.
   */
  public java.lang.CharSequence getRiskMeasure() {
    return RiskMeasure;
  }

  /**
   * Sets the value of the 'RiskMeasure' field.
   * @param value the value to set.
   */
  public void setRiskMeasure(java.lang.CharSequence value) {
    this.RiskMeasure = value;
  }

  /**
   * Gets the value of the 'Parameter' field.
   * @return The value of the 'Parameter' field.
   */
  public java.lang.CharSequence getParameter() {
    return Parameter;
  }

  /**
   * Sets the value of the 'Parameter' field.
   * @param value the value to set.
   */
  public void setParameter(java.lang.CharSequence value) {
    this.Parameter = value;
  }

  /**
   * Gets the value of the 'RiskValue' field.
   * @return The value of the 'RiskValue' field.
   */
  public gsp.RiskAmount getRiskValue() {
    return RiskValue;
  }

  /**
   * Sets the value of the 'RiskValue' field.
   * @param value the value to set.
   */
  public void setRiskValue(gsp.RiskAmount value) {
    this.RiskValue = value;
  }

  /**
   * Creates a new TradeRiskValue RecordBuilder.
   * @return A new TradeRiskValue RecordBuilder
   */
  public static gsp.TradeRiskValue.Builder newBuilder() {
    return new gsp.TradeRiskValue.Builder();
  }

  /**
   * Creates a new TradeRiskValue RecordBuilder by copying an existing Builder.
   * @param other The existing builder to copy.
   * @return A new TradeRiskValue RecordBuilder
   */
  public static gsp.TradeRiskValue.Builder newBuilder(gsp.TradeRiskValue.Builder other) {
    return new gsp.TradeRiskValue.Builder(other);
  }

  /**
   * Creates a new TradeRiskValue RecordBuilder by copying an existing TradeRiskValue instance.
   * @param other The existing instance to copy.
   * @return A new TradeRiskValue RecordBuilder
   */
  public static gsp.TradeRiskValue.Builder newBuilder(gsp.TradeRiskValue other) {
    return new gsp.TradeRiskValue.Builder(other);
  }

  /**
   * RecordBuilder for TradeRiskValue instances.
   */
  public static class Builder extends org.apache.avro.specific.SpecificRecordBuilderBase<TradeRiskValue>
    implements org.apache.avro.data.RecordBuilder<TradeRiskValue> {

    private java.lang.CharSequence RiskMeasure;
    private java.lang.CharSequence Parameter;
    private gsp.RiskAmount RiskValue;
    private gsp.RiskAmount.Builder RiskValueBuilder;

    /** Creates a new Builder */
    private Builder() {
      super(SCHEMA$);
    }

    /**
     * Creates a Builder by copying an existing Builder.
     * @param other The existing Builder to copy.
     */
    private Builder(gsp.TradeRiskValue.Builder other) {
      super(other);
      if (isValidValue(fields()[0], other.RiskMeasure)) {
        this.RiskMeasure = data().deepCopy(fields()[0].schema(), other.RiskMeasure);
        fieldSetFlags()[0] = true;
      }
      if (isValidValue(fields()[1], other.Parameter)) {
        this.Parameter = data().deepCopy(fields()[1].schema(), other.Parameter);
        fieldSetFlags()[1] = true;
      }
      if (isValidValue(fields()[2], other.RiskValue)) {
        this.RiskValue = data().deepCopy(fields()[2].schema(), other.RiskValue);
        fieldSetFlags()[2] = true;
      }
      if (other.hasRiskValueBuilder()) {
        this.RiskValueBuilder = gsp.RiskAmount.newBuilder(other.getRiskValueBuilder());
      }
    }

    /**
     * Creates a Builder by copying an existing TradeRiskValue instance
     * @param other The existing instance to copy.
     */
    private Builder(gsp.TradeRiskValue other) {
            super(SCHEMA$);
      if (isValidValue(fields()[0], other.RiskMeasure)) {
        this.RiskMeasure = data().deepCopy(fields()[0].schema(), other.RiskMeasure);
        fieldSetFlags()[0] = true;
      }
      if (isValidValue(fields()[1], other.Parameter)) {
        this.Parameter = data().deepCopy(fields()[1].schema(), other.Parameter);
        fieldSetFlags()[1] = true;
      }
      if (isValidValue(fields()[2], other.RiskValue)) {
        this.RiskValue = data().deepCopy(fields()[2].schema(), other.RiskValue);
        fieldSetFlags()[2] = true;
      }
      this.RiskValueBuilder = null;
    }

    /**
      * Gets the value of the 'RiskMeasure' field.
      * @return The value.
      */
    public java.lang.CharSequence getRiskMeasure() {
      return RiskMeasure;
    }

    /**
      * Sets the value of the 'RiskMeasure' field.
      * @param value The value of 'RiskMeasure'.
      * @return This builder.
      */
    public gsp.TradeRiskValue.Builder setRiskMeasure(java.lang.CharSequence value) {
      validate(fields()[0], value);
      this.RiskMeasure = value;
      fieldSetFlags()[0] = true;
      return this;
    }

    /**
      * Checks whether the 'RiskMeasure' field has been set.
      * @return True if the 'RiskMeasure' field has been set, false otherwise.
      */
    public boolean hasRiskMeasure() {
      return fieldSetFlags()[0];
    }


    /**
      * Clears the value of the 'RiskMeasure' field.
      * @return This builder.
      */
    public gsp.TradeRiskValue.Builder clearRiskMeasure() {
      RiskMeasure = null;
      fieldSetFlags()[0] = false;
      return this;
    }

    /**
      * Gets the value of the 'Parameter' field.
      * @return The value.
      */
    public java.lang.CharSequence getParameter() {
      return Parameter;
    }

    /**
      * Sets the value of the 'Parameter' field.
      * @param value The value of 'Parameter'.
      * @return This builder.
      */
    public gsp.TradeRiskValue.Builder setParameter(java.lang.CharSequence value) {
      validate(fields()[1], value);
      this.Parameter = value;
      fieldSetFlags()[1] = true;
      return this;
    }

    /**
      * Checks whether the 'Parameter' field has been set.
      * @return True if the 'Parameter' field has been set, false otherwise.
      */
    public boolean hasParameter() {
      return fieldSetFlags()[1];
    }


    /**
      * Clears the value of the 'Parameter' field.
      * @return This builder.
      */
    public gsp.TradeRiskValue.Builder clearParameter() {
      Parameter = null;
      fieldSetFlags()[1] = false;
      return this;
    }

    /**
      * Gets the value of the 'RiskValue' field.
      * @return The value.
      */
    public gsp.RiskAmount getRiskValue() {
      return RiskValue;
    }

    /**
      * Sets the value of the 'RiskValue' field.
      * @param value The value of 'RiskValue'.
      * @return This builder.
      */
    public gsp.TradeRiskValue.Builder setRiskValue(gsp.RiskAmount value) {
      validate(fields()[2], value);
      this.RiskValueBuilder = null;
      this.RiskValue = value;
      fieldSetFlags()[2] = true;
      return this;
    }

    /**
      * Checks whether the 'RiskValue' field has been set.
      * @return True if the 'RiskValue' field has been set, false otherwise.
      */
    public boolean hasRiskValue() {
      return fieldSetFlags()[2];
    }

    /**
     * Gets the Builder instance for the 'RiskValue' field and creates one if it doesn't exist yet.
     * @return This builder.
     */
    public gsp.RiskAmount.Builder getRiskValueBuilder() {
      if (RiskValueBuilder == null) {
        if (hasRiskValue()) {
          setRiskValueBuilder(gsp.RiskAmount.newBuilder(RiskValue));
        } else {
          setRiskValueBuilder(gsp.RiskAmount.newBuilder());
        }
      }
      return RiskValueBuilder;
    }

    /**
     * Sets the Builder instance for the 'RiskValue' field
     * @param value The builder instance that must be set.
     * @return This builder.
     */
    public gsp.TradeRiskValue.Builder setRiskValueBuilder(gsp.RiskAmount.Builder value) {
      clearRiskValue();
      RiskValueBuilder = value;
      return this;
    }

    /**
     * Checks whether the 'RiskValue' field has an active Builder instance
     * @return True if the 'RiskValue' field has an active Builder instance
     */
    public boolean hasRiskValueBuilder() {
      return RiskValueBuilder != null;
    }

    /**
      * Clears the value of the 'RiskValue' field.
      * @return This builder.
      */
    public gsp.TradeRiskValue.Builder clearRiskValue() {
      RiskValue = null;
      RiskValueBuilder = null;
      fieldSetFlags()[2] = false;
      return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public TradeRiskValue build() {
      try {
        TradeRiskValue record = new TradeRiskValue();
        record.RiskMeasure = fieldSetFlags()[0] ? this.RiskMeasure : (java.lang.CharSequence) defaultValue(fields()[0]);
        record.Parameter = fieldSetFlags()[1] ? this.Parameter : (java.lang.CharSequence) defaultValue(fields()[1]);
        if (RiskValueBuilder != null) {
          record.RiskValue = this.RiskValueBuilder.build();
        } else {
          record.RiskValue = fieldSetFlags()[2] ? this.RiskValue : (gsp.RiskAmount) defaultValue(fields()[2]);
        }
        return record;
      } catch (java.lang.Exception e) {
        throw new org.apache.avro.AvroRuntimeException(e);
      }
    }
  }

  @SuppressWarnings("unchecked")
  private static final org.apache.avro.io.DatumWriter<TradeRiskValue>
    WRITER$ = (org.apache.avro.io.DatumWriter<TradeRiskValue>)MODEL$.createDatumWriter(SCHEMA$);

  @Override public void writeExternal(java.io.ObjectOutput out)
    throws java.io.IOException {
    WRITER$.write(this, SpecificData.getEncoder(out));
  }

  @SuppressWarnings("unchecked")
  private static final org.apache.avro.io.DatumReader<TradeRiskValue>
    READER$ = (org.apache.avro.io.DatumReader<TradeRiskValue>)MODEL$.createDatumReader(SCHEMA$);

  @Override public void readExternal(java.io.ObjectInput in)
    throws java.io.IOException {
    READER$.read(this, SpecificData.getDecoder(in));
  }

}