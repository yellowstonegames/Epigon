package squidpony.epigon.data;

/**
 * Created by Tommy Ettinger on 2/9/2019.
 */
public class VisualCondition extends EpiData {
    public float lumaChange, orangeChange, greenChange;
    public VisualCondition()
    {
        lumaChange = 0f;
        orangeChange = 0f;
        greenChange = 0f;
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
