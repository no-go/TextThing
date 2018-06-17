package click.dummer.textthing;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

// unused
public class CustomTextView extends android.support.v7.widget.AppCompatTextView {

    public CustomTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        try {
            TypedArray a = context.obtainStyledAttributes(attrs,
                    R.styleable.font, defStyle, 0);

            String str = a.getString(R.styleable.font_fonttype);
            a.recycle();
            switch (Integer.parseInt(str)) {
                case 0:
                    str = "fonts/c64pro_mono.ttf";
                    break;
                default:
                    break;
            }

            setTypeface(FontManager.getInstance(getContext()).loadFont(str));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public CustomTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    @SuppressWarnings("unused")
    private void internalInit(Context context, AttributeSet attrs) {

    }
}