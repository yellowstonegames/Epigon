package squidpony.epigon.data;

/**
 * Created by Tommy Ettinger on 2/9/2019.
 */
public class VisualCondition {
    public float lumaChange, orangeChange, greenChange;
    public VisualCondition()
    {
        lumaChange = 1f;
        orangeChange = 1f;
        greenChange = 1f;
    }
    public VisualCondition(float luma, float orange, float green)
    {
        lumaChange = luma;
        orangeChange = orange;
        greenChange = green;
    }
    public void update()
    {
        
    }
}
