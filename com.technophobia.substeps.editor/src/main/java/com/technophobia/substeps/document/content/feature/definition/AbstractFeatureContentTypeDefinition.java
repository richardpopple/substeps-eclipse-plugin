package com.technophobia.substeps.document.content.feature.definition;

import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.EndOfLineRule;
import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.IWordDetector;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WordRule;

import com.technophobia.substeps.colour.ColourManager;
import com.technophobia.substeps.document.content.ContentTypeDefinition;
import com.technophobia.substeps.document.content.feature.FeatureColour;
import com.technophobia.substeps.document.text.rule.UntilOtherContentTypeSequenceRule;

public abstract class AbstractFeatureContentTypeDefinition implements ContentTypeDefinition {

    private final String id;
    private final String prefixText;
    private final boolean optional;


    public AbstractFeatureContentTypeDefinition(final String id, final String prefixText, final boolean optional) {
        super();
        this.id = id;
		this.prefixText = prefixText;
        this.optional = optional;
    }


    @Override
    public String id() {
        return id;
    }
    
    @Override
    public String prefixText(){
    	return prefixText;
    }


    @Override
    public boolean isOptional() {
        return optional;
    }


    protected IToken colourToken(final FeatureColour colour, final ColourManager colourManager) {
        return new Token(new TextAttribute(colourManager.getColor(colour.colour())));
    }


    protected IRule fixedWordRule(final String word, final IToken token) {
        return new WordRule(word(word), token);
    }


    protected IPredicateRule singleLineRule(final String startString, final String tokenId) {
        return singleLineRule(startString, new Token(tokenId));
    }
    
    protected IPredicateRule singleLineRule(final String startSequence, final IToken token) {
        return new EndOfLineRule(startSequence, token);
    }


    protected IPredicateRule paragraphRule(final String startString, final String tokenId, final boolean breaksOnEOF, final String... validProceedingContentTypes) {
        return paragraphRule(startString, new Token(tokenId), breaksOnEOF, validProceedingContentTypes);
    }

    protected IPredicateRule paragraphRule(final String startString, final IToken token, boolean breaksOnEOF, final String... validProceedingContentTypes) {
        return new UntilOtherContentTypeSequenceRule(startString, token, breaksOnEOF, validProceedingContentTypes);
    }

    protected IWordDetector word(final String word) {
        return new IWordDetector() {
            int i = 0;


            @Override
            public boolean isWordStart(final char c) {
                if (word.charAt(0) == c) {
                    i = 0;
                    return true;
                }
                return false;
            }


            @Override
            public boolean isWordPart(final char c) {
                i++;
                return word.length() > i && word.charAt(i) == c;
            }
        };
    }
}
