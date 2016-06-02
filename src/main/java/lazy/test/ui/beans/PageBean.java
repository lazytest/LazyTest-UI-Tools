package lazy.test.ui.beans;

import java.lang.reflect.Field;

import lazy.test.ui.annotations.Description;
import lazy.test.ui.annotations.Frame;
import lazy.test.ui.annotations.TextContent;
import lazy.test.ui.annotations.Xpath;
import lazy.test.ui.browser.BrowseEmulator;
import lazy.test.ui.controls.ControlType;

/**
 * Created by sushidong on 2016/3/28.
 */
public class PageBean {

    protected BrowseEmulator be;

    public PageBean(BrowseEmulator be) {
        this.be = be;
        try {
            Field[] fields = this.getClass().getDeclaredFields();

            for (Field field : fields) {
                try {
                    ControlType.valueOf(field.getType().getSimpleName().toUpperCase());
                } catch (Exception e) {
                    continue;
                }

                field.setAccessible(true);
                Object object = field.getType().newInstance();

                if (field.isAnnotationPresent(Xpath.class)) {
                    String[] xpath = field.getAnnotation(Xpath.class).xpath();

                    Field xpathField = getAbstractControl(field).getDeclaredField("xpath");
                    xpathField.setAccessible(true);
                    xpathField.set(object, xpath);
                }

                if (field.isAnnotationPresent(TextContent.class)) {
                    String[] text = field.getAnnotation(TextContent.class).textContent();

                    Field xpathField = getAbstractControl(field).getDeclaredField("textContent");
                    xpathField.setAccessible(true);
                    xpathField.set(object, text);
                }

                if (field.isAnnotationPresent(Frame.class)) {
                    String frame = field.getAnnotation(Frame.class).frame();

                    Field frameField = getAbstractControl(field).getDeclaredField("frame");
                    frameField.setAccessible(true);
                    frameField.set(object, frame);
                }

                if (field.isAnnotationPresent(Description.class)) {
                    String description = field.getAnnotation(Description.class).description();

                    Field descField = getAbstractControl(field).getDeclaredField("description");
                    descField.setAccessible(true);
                    descField.set(object, description);
                }

                Field beField = getAbstractControl(field).getDeclaredField("be");
                beField.setAccessible(true);
                beField.set(object, be);

                field.set(this, object);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Class<?> getAbstractControl(Field field) {
        Class<?> abstractControl = field.getType();

        while(!abstractControl.getSimpleName().equals("AbstractControl")) {
            abstractControl = abstractControl.getSuperclass();
        }

        return abstractControl;
    }

}
