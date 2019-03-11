package squidpony.epigon.data;

/**
 * Created by Tommy Ettinger on 2/9/2019.
 */
public class VisualCondition {
    public float lumaMul, warmMul, mildMul, lumaAdd, warmAdd, mildAdd;
    public VisualCondition()
    {
        lumaMul = 1f;
        warmMul = 1f;
        mildMul = 1f;
    }
    public VisualCondition(float luma, float warm, float mild)
    {
        lumaMul = luma;
        warmMul = warm;
        mildMul = mild;
    }
    public VisualCondition(float lumaMul, float warmMul, float mildMul, float lumaAdd, float warmAdd, float mildAdd)
    {
        this.lumaMul = lumaMul;
        this.warmMul = warmMul;
        this.mildMul = mildMul;
        this.lumaAdd = lumaAdd;
        this.warmAdd = warmAdd;
        this.mildAdd = mildAdd;
    }
    public void update()
    {
        
    }
}
