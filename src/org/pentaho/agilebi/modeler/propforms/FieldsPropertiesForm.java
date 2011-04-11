package org.pentaho.agilebi.modeler.propforms;

import org.pentaho.agilebi.modeler.nodes.BaseAggregationMetaDataNode;
import org.pentaho.metadata.model.concept.types.AggregationType;
import org.pentaho.ui.xul.binding.Binding;
import org.pentaho.ui.xul.binding.BindingConvertor;
import org.pentaho.ui.xul.containers.XulDeck;
import org.pentaho.ui.xul.containers.XulVbox;
import org.pentaho.ui.xul.stereotype.Bindable;

import java.util.*;

/**
 * Created: 3/24/11
 *
 * @author rfellows
 */
public class FieldsPropertiesForm extends MeasuresPropertiesForm {

  private static final String ID = "fieldprops";
  private Collection<AggregationType> selectedAggregations;

  public FieldsPropertiesForm(String locale) {
    super(ID, locale);
  }

  public FieldsPropertiesForm(String panelId, String locale) {
    super(panelId, locale);
  }

  @Override
  @Bindable
  public void init() {
    deck = (XulDeck) document.getElementById("propertiesdeck");
    panel = (XulVbox) document.getElementById(ID);

    bf.createBinding(this, "notValid", "fieldmessages", "visible");
    bf.createBinding(this, "validMessages", "fieldmessageslabel", "value");
    bf.createBinding(this, "displayName", "fielddisplayname", "value");
    bf.createBinding(this, "possibleAggregations", "field_possibleAggregationTypes", "elements");
    bf.createBinding(this, "selectedAggregations", "field_possibleAggregationTypes", "selectedItems", BindingConvertor.collection2ObjectArray());
    bf.createBinding(this, "selectedAggregations", "field_defaultAggregation", "elements");
    bf.createBinding(this, "defaultAggregation", "field_defaultAggregation", "selectedItem");

    bf.createBinding(this, "format", "fieldformatstring", "selectedItem", new FormatStringConverter());
    bf.createBinding(this, "notValid", "fixFieldColumnsBtn", "visible");
    bf.createBinding(this, "columnName", "field_column_name", "value");

  }

  @Override
  public void setObject(BaseAggregationMetaDataNode t) {
    selectedAggregations = null;
    super.setObject(t);

    // curent aggregation is blown away when setting the potential list. Cache it and reset it on the other-side
    AggregationType aggType = t.getDefaultAggregation();
    setSelectedAggregations(t.getSelectedAggregations());
    setDefaultAggregation(aggType);     // reset default
  }


  @Bindable
  @Override
  public void setPossibleAggregations( Vector aggTypes ) {
    this.aggTypes = aggTypes;
    if (fieldMeta != null && fieldMeta.getSelectedAggregations() == null) {
      fieldMeta.setSelectedAggregations(aggTypes);
    }
    this.firePropertyChange("possibleAggregations", null, aggTypes);
  }

  @Bindable
  public void setSelectedAggregations(Collection<AggregationType> selectedAggs){
    Collection<AggregationType> prevVal = selectedAggregations;
    selectedAggregations = selectedAggs;
    if(fieldMeta != null){
      fieldMeta.setSelectedAggregations(new ArrayList<AggregationType>(selectedAggs));
    }
    if(prevVal == null || prevVal.equals(selectedAggregations) == false){
      this.firePropertyChange("selectedAggregations", null, selectedAggregations);
    }
    // this will clear the selection of the default aggregate. Reselect it here

  }

  @Bindable
  public List<AggregationType> getSelectedAggregations(){
    if(fieldMeta != null){
      return fieldMeta.getSelectedAggregations();
    }
    return Collections.emptyList();
  }
  private static class FormatStringConverter extends BindingConvertor<String, String> {

    @Override
    public String sourceToTarget( String value ) {
      if (value == null) {
        return "NONE";
      } else {
        return value;
      }
    }

    @Override
    public String targetToSource( String value ) {
      if (value.equalsIgnoreCase("NONE")) {
        return null;
      } else {
        return value;
      }
    }

  }
}
