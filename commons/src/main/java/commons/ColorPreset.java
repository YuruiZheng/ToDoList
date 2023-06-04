package commons;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name="color_preset")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ColorPreset implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Integer boardId;
    private String name;
    private String background;
    private String font;

    public void update(ColorPreset cp) {
        if(!Objects.isNull(cp.name)) {
            this.name = cp.name;
        }
        if(!Objects.isNull(cp.background)) {
            this.background = cp.background;
        }
        if(!Objects.isNull(cp.font)) {
            this.font = cp.font;
        }
    }

    @Override
    public String toString() {
        return this.name;
    }
}
