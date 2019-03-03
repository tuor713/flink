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
public class FirmAccount extends org.apache.avro.specific.SpecificRecordBase implements org.apache.avro.specific.SpecificRecord {
  private static final long serialVersionUID = -3294572961867138196L;
  public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{\"type\":\"record\",\"name\":\"FirmAccount\",\"namespace\":\"gsp\",\"fields\":[{\"name\":\"Mnemonic\",\"type\":\"string\"},{\"name\":\"LegalEntityCode\",\"type\":\"string\"},{\"name\":\"StrategyCode\",\"type\":\"string\"},{\"name\":\"GOC\",\"type\":[\"null\",\"string\"],\"default\":null}]}");
  public static org.apache.avro.Schema getClassSchema() { return SCHEMA$; }

  private static SpecificData MODEL$ = new SpecificData();

  private static final BinaryMessageEncoder<FirmAccount> ENCODER =
      new BinaryMessageEncoder<FirmAccount>(MODEL$, SCHEMA$);

  private static final BinaryMessageDecoder<FirmAccount> DECODER =
      new BinaryMessageDecoder<FirmAccount>(MODEL$, SCHEMA$);

  /**
   * Return the BinaryMessageDecoder instance used by this class.
   */
  public static BinaryMessageDecoder<FirmAccount> getDecoder() {
    return DECODER;
  }

  /**
   * Create a new BinaryMessageDecoder instance for this class that uses the specified {@link SchemaStore}.
   * @param resolver a {@link SchemaStore} used to find schemas by fingerprint
   */
  public static BinaryMessageDecoder<FirmAccount> createDecoder(SchemaStore resolver) {
    return new BinaryMessageDecoder<FirmAccount>(MODEL$, SCHEMA$, resolver);
  }

  /** Serializes this FirmAccount to a ByteBuffer. */
  public java.nio.ByteBuffer toByteBuffer() throws java.io.IOException {
    return ENCODER.encode(this);
  }

  /** Deserializes a FirmAccount from a ByteBuffer. */
  public static FirmAccount fromByteBuffer(
      java.nio.ByteBuffer b) throws java.io.IOException {
    return DECODER.decode(b);
  }

  @Deprecated public java.lang.CharSequence Mnemonic;
  @Deprecated public java.lang.CharSequence LegalEntityCode;
  @Deprecated public java.lang.CharSequence StrategyCode;
  @Deprecated public java.lang.CharSequence GOC;

  /**
   * Default constructor.  Note that this does not initialize fields
   * to their default values from the schema.  If that is desired then
   * one should use <code>newBuilder()</code>.
   */
  public FirmAccount() {}

  /**
   * All-args constructor.
   * @param Mnemonic The new value for Mnemonic
   * @param LegalEntityCode The new value for LegalEntityCode
   * @param StrategyCode The new value for StrategyCode
   * @param GOC The new value for GOC
   */
  public FirmAccount(java.lang.CharSequence Mnemonic, java.lang.CharSequence LegalEntityCode, java.lang.CharSequence StrategyCode, java.lang.CharSequence GOC) {
    this.Mnemonic = Mnemonic;
    this.LegalEntityCode = LegalEntityCode;
    this.StrategyCode = StrategyCode;
    this.GOC = GOC;
  }

  public org.apache.avro.Schema getSchema() { return SCHEMA$; }
  // Used by DatumWriter.  Applications should not call.
  public java.lang.Object get(int field$) {
    switch (field$) {
    case 0: return Mnemonic;
    case 1: return LegalEntityCode;
    case 2: return StrategyCode;
    case 3: return GOC;
    default: throw new org.apache.avro.AvroRuntimeException("Bad index");
    }
  }

  // Used by DatumReader.  Applications should not call.
  @SuppressWarnings(value="unchecked")
  public void put(int field$, java.lang.Object value$) {
    switch (field$) {
    case 0: Mnemonic = (java.lang.CharSequence)value$; break;
    case 1: LegalEntityCode = (java.lang.CharSequence)value$; break;
    case 2: StrategyCode = (java.lang.CharSequence)value$; break;
    case 3: GOC = (java.lang.CharSequence)value$; break;
    default: throw new org.apache.avro.AvroRuntimeException("Bad index");
    }
  }

  /**
   * Gets the value of the 'Mnemonic' field.
   * @return The value of the 'Mnemonic' field.
   */
  public java.lang.CharSequence getMnemonic() {
    return Mnemonic;
  }

  /**
   * Sets the value of the 'Mnemonic' field.
   * @param value the value to set.
   */
  public void setMnemonic(java.lang.CharSequence value) {
    this.Mnemonic = value;
  }

  /**
   * Gets the value of the 'LegalEntityCode' field.
   * @return The value of the 'LegalEntityCode' field.
   */
  public java.lang.CharSequence getLegalEntityCode() {
    return LegalEntityCode;
  }

  /**
   * Sets the value of the 'LegalEntityCode' field.
   * @param value the value to set.
   */
  public void setLegalEntityCode(java.lang.CharSequence value) {
    this.LegalEntityCode = value;
  }

  /**
   * Gets the value of the 'StrategyCode' field.
   * @return The value of the 'StrategyCode' field.
   */
  public java.lang.CharSequence getStrategyCode() {
    return StrategyCode;
  }

  /**
   * Sets the value of the 'StrategyCode' field.
   * @param value the value to set.
   */
  public void setStrategyCode(java.lang.CharSequence value) {
    this.StrategyCode = value;
  }

  /**
   * Gets the value of the 'GOC' field.
   * @return The value of the 'GOC' field.
   */
  public java.lang.CharSequence getGOC() {
    return GOC;
  }

  /**
   * Sets the value of the 'GOC' field.
   * @param value the value to set.
   */
  public void setGOC(java.lang.CharSequence value) {
    this.GOC = value;
  }

  /**
   * Creates a new FirmAccount RecordBuilder.
   * @return A new FirmAccount RecordBuilder
   */
  public static gsp.FirmAccount.Builder newBuilder() {
    return new gsp.FirmAccount.Builder();
  }

  /**
   * Creates a new FirmAccount RecordBuilder by copying an existing Builder.
   * @param other The existing builder to copy.
   * @return A new FirmAccount RecordBuilder
   */
  public static gsp.FirmAccount.Builder newBuilder(gsp.FirmAccount.Builder other) {
    return new gsp.FirmAccount.Builder(other);
  }

  /**
   * Creates a new FirmAccount RecordBuilder by copying an existing FirmAccount instance.
   * @param other The existing instance to copy.
   * @return A new FirmAccount RecordBuilder
   */
  public static gsp.FirmAccount.Builder newBuilder(gsp.FirmAccount other) {
    return new gsp.FirmAccount.Builder(other);
  }

  /**
   * RecordBuilder for FirmAccount instances.
   */
  public static class Builder extends org.apache.avro.specific.SpecificRecordBuilderBase<FirmAccount>
    implements org.apache.avro.data.RecordBuilder<FirmAccount> {

    private java.lang.CharSequence Mnemonic;
    private java.lang.CharSequence LegalEntityCode;
    private java.lang.CharSequence StrategyCode;
    private java.lang.CharSequence GOC;

    /** Creates a new Builder */
    private Builder() {
      super(SCHEMA$);
    }

    /**
     * Creates a Builder by copying an existing Builder.
     * @param other The existing Builder to copy.
     */
    private Builder(gsp.FirmAccount.Builder other) {
      super(other);
      if (isValidValue(fields()[0], other.Mnemonic)) {
        this.Mnemonic = data().deepCopy(fields()[0].schema(), other.Mnemonic);
        fieldSetFlags()[0] = true;
      }
      if (isValidValue(fields()[1], other.LegalEntityCode)) {
        this.LegalEntityCode = data().deepCopy(fields()[1].schema(), other.LegalEntityCode);
        fieldSetFlags()[1] = true;
      }
      if (isValidValue(fields()[2], other.StrategyCode)) {
        this.StrategyCode = data().deepCopy(fields()[2].schema(), other.StrategyCode);
        fieldSetFlags()[2] = true;
      }
      if (isValidValue(fields()[3], other.GOC)) {
        this.GOC = data().deepCopy(fields()[3].schema(), other.GOC);
        fieldSetFlags()[3] = true;
      }
    }

    /**
     * Creates a Builder by copying an existing FirmAccount instance
     * @param other The existing instance to copy.
     */
    private Builder(gsp.FirmAccount other) {
            super(SCHEMA$);
      if (isValidValue(fields()[0], other.Mnemonic)) {
        this.Mnemonic = data().deepCopy(fields()[0].schema(), other.Mnemonic);
        fieldSetFlags()[0] = true;
      }
      if (isValidValue(fields()[1], other.LegalEntityCode)) {
        this.LegalEntityCode = data().deepCopy(fields()[1].schema(), other.LegalEntityCode);
        fieldSetFlags()[1] = true;
      }
      if (isValidValue(fields()[2], other.StrategyCode)) {
        this.StrategyCode = data().deepCopy(fields()[2].schema(), other.StrategyCode);
        fieldSetFlags()[2] = true;
      }
      if (isValidValue(fields()[3], other.GOC)) {
        this.GOC = data().deepCopy(fields()[3].schema(), other.GOC);
        fieldSetFlags()[3] = true;
      }
    }

    /**
      * Gets the value of the 'Mnemonic' field.
      * @return The value.
      */
    public java.lang.CharSequence getMnemonic() {
      return Mnemonic;
    }

    /**
      * Sets the value of the 'Mnemonic' field.
      * @param value The value of 'Mnemonic'.
      * @return This builder.
      */
    public gsp.FirmAccount.Builder setMnemonic(java.lang.CharSequence value) {
      validate(fields()[0], value);
      this.Mnemonic = value;
      fieldSetFlags()[0] = true;
      return this;
    }

    /**
      * Checks whether the 'Mnemonic' field has been set.
      * @return True if the 'Mnemonic' field has been set, false otherwise.
      */
    public boolean hasMnemonic() {
      return fieldSetFlags()[0];
    }


    /**
      * Clears the value of the 'Mnemonic' field.
      * @return This builder.
      */
    public gsp.FirmAccount.Builder clearMnemonic() {
      Mnemonic = null;
      fieldSetFlags()[0] = false;
      return this;
    }

    /**
      * Gets the value of the 'LegalEntityCode' field.
      * @return The value.
      */
    public java.lang.CharSequence getLegalEntityCode() {
      return LegalEntityCode;
    }

    /**
      * Sets the value of the 'LegalEntityCode' field.
      * @param value The value of 'LegalEntityCode'.
      * @return This builder.
      */
    public gsp.FirmAccount.Builder setLegalEntityCode(java.lang.CharSequence value) {
      validate(fields()[1], value);
      this.LegalEntityCode = value;
      fieldSetFlags()[1] = true;
      return this;
    }

    /**
      * Checks whether the 'LegalEntityCode' field has been set.
      * @return True if the 'LegalEntityCode' field has been set, false otherwise.
      */
    public boolean hasLegalEntityCode() {
      return fieldSetFlags()[1];
    }


    /**
      * Clears the value of the 'LegalEntityCode' field.
      * @return This builder.
      */
    public gsp.FirmAccount.Builder clearLegalEntityCode() {
      LegalEntityCode = null;
      fieldSetFlags()[1] = false;
      return this;
    }

    /**
      * Gets the value of the 'StrategyCode' field.
      * @return The value.
      */
    public java.lang.CharSequence getStrategyCode() {
      return StrategyCode;
    }

    /**
      * Sets the value of the 'StrategyCode' field.
      * @param value The value of 'StrategyCode'.
      * @return This builder.
      */
    public gsp.FirmAccount.Builder setStrategyCode(java.lang.CharSequence value) {
      validate(fields()[2], value);
      this.StrategyCode = value;
      fieldSetFlags()[2] = true;
      return this;
    }

    /**
      * Checks whether the 'StrategyCode' field has been set.
      * @return True if the 'StrategyCode' field has been set, false otherwise.
      */
    public boolean hasStrategyCode() {
      return fieldSetFlags()[2];
    }


    /**
      * Clears the value of the 'StrategyCode' field.
      * @return This builder.
      */
    public gsp.FirmAccount.Builder clearStrategyCode() {
      StrategyCode = null;
      fieldSetFlags()[2] = false;
      return this;
    }

    /**
      * Gets the value of the 'GOC' field.
      * @return The value.
      */
    public java.lang.CharSequence getGOC() {
      return GOC;
    }

    /**
      * Sets the value of the 'GOC' field.
      * @param value The value of 'GOC'.
      * @return This builder.
      */
    public gsp.FirmAccount.Builder setGOC(java.lang.CharSequence value) {
      validate(fields()[3], value);
      this.GOC = value;
      fieldSetFlags()[3] = true;
      return this;
    }

    /**
      * Checks whether the 'GOC' field has been set.
      * @return True if the 'GOC' field has been set, false otherwise.
      */
    public boolean hasGOC() {
      return fieldSetFlags()[3];
    }


    /**
      * Clears the value of the 'GOC' field.
      * @return This builder.
      */
    public gsp.FirmAccount.Builder clearGOC() {
      GOC = null;
      fieldSetFlags()[3] = false;
      return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public FirmAccount build() {
      try {
        FirmAccount record = new FirmAccount();
        record.Mnemonic = fieldSetFlags()[0] ? this.Mnemonic : (java.lang.CharSequence) defaultValue(fields()[0]);
        record.LegalEntityCode = fieldSetFlags()[1] ? this.LegalEntityCode : (java.lang.CharSequence) defaultValue(fields()[1]);
        record.StrategyCode = fieldSetFlags()[2] ? this.StrategyCode : (java.lang.CharSequence) defaultValue(fields()[2]);
        record.GOC = fieldSetFlags()[3] ? this.GOC : (java.lang.CharSequence) defaultValue(fields()[3]);
        return record;
      } catch (java.lang.Exception e) {
        throw new org.apache.avro.AvroRuntimeException(e);
      }
    }
  }

  @SuppressWarnings("unchecked")
  private static final org.apache.avro.io.DatumWriter<FirmAccount>
    WRITER$ = (org.apache.avro.io.DatumWriter<FirmAccount>)MODEL$.createDatumWriter(SCHEMA$);

  @Override public void writeExternal(java.io.ObjectOutput out)
    throws java.io.IOException {
    WRITER$.write(this, SpecificData.getEncoder(out));
  }

  @SuppressWarnings("unchecked")
  private static final org.apache.avro.io.DatumReader<FirmAccount>
    READER$ = (org.apache.avro.io.DatumReader<FirmAccount>)MODEL$.createDatumReader(SCHEMA$);

  @Override public void readExternal(java.io.ObjectInput in)
    throws java.io.IOException {
    READER$.read(this, SpecificData.getDecoder(in));
  }

}
