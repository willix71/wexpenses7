import java.util.UUID;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.modelmapper.ModelMapper;

@Ignore
public class CategoryTest {

    public static class Category {
        private String uid = UUID.randomUUID().toString();
        private String name;
        private Category parent;

        public Category () {}

        public Category(String name, Category parent) {
            this.name = name;
            this.parent = parent;
        }

        public String getUid() {return uid;}
        public void setUid(String uid) {this.uid = uid;}
        public String getName() {return name;}
        public void setName(String name) {this.name = name;}
        public Category getParent() {return parent;}
        public void setParent(Category parent) {this.parent = parent;}
    }

    public static class AbstractDTO {
        private String uid;
        private String name;

        public String getUid() {return uid;}
        public void setUid(String uid) {this.uid = uid;}
        public String getName() {return name;}
        public void setName(String name) {this.name = name;}
    }

    public static class CategoryDTO extends AbstractDTO {
        private AbstractDTO parent;

        public AbstractDTO getParent() {return parent;}
        public void setParent(AbstractDTO parent) {this.parent = parent;}
    }

    @Test
    public void simpleTest() {
        Category dto = new Category("Test1",null);

        CategoryDTO entity = new ModelMapper().map(dto, CategoryDTO.class);

        Assert.assertEquals("Test1", entity.getName());
        Assert.assertEquals(dto.getUid(), entity.getUid());
    }

    @Test
    public void withParentTest() {
        Category dto = new Category("child",new Category("parent", new Category("root", null)));

        CategoryDTO entity = new ModelMapper().map(dto, CategoryDTO.class);

        Assert.assertEquals("child", entity.getName());
        Assert.assertEquals(dto.getUid(), entity.getUid());
        Assert.assertNotNull(entity.getParent()); // my child should have a parent dto
        Assert.assertTrue(entity.getParent() instanceof AbstractDTO); // and it should be an AbstractDTO to stop
    }
}