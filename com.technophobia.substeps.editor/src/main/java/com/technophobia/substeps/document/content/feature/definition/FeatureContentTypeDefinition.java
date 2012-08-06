package com.technophobia.substeps.document.content.feature.definition;

import org.eclipse.jface.text.formatter.IFormattingStrategy;
import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.IRule;

import com.technophobia.substeps.colour.ColourManager;
import com.technophobia.substeps.document.content.feature.FeatureColour;
import com.technophobia.substeps.document.formatting.FormattingContext;
import com.technophobia.substeps.document.formatting.strategy.MultiLineFixedIndentFormattingStrategy;
import com.technophobia.substeps.supplier.Supplier;

public class FeatureContentTypeDefinition extends AbstractFeatureContentTypeDefinition {

    public static final String CONTENT_TYPE_ID = "__feature_feature";


    public FeatureContentTypeDefinition() {
        super(CONTENT_TYPE_ID, false);
    }


    @Override
    public IPredicateRule partitionRule() {
        return paragraphRule("Feature:", id());
    }


    @Override
    public IRule damageRepairerRule(final ColourManager colourManager) {
        return fixedWordRule("Feature:", colourToken(FeatureColour.BLUE, colourManager));
    }


    @Override
    public IFormattingStrategy formattingStrategy(final Supplier<FormattingContext> formattingContextSupplier) {
        return new MultiLineFixedIndentFormattingStrategy("", "\t\t");
    }
}
