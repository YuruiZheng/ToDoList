package commons;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "tag")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Tag implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Integer boardId;
    private String title;
    private String color;

    public void applyUpdates(Tag tag)    {
        if (!Objects.isNull(tag.boardId))
            this.boardId = tag.boardId;
        if (!Objects.isNull(tag.title))
            this.title = tag.title;
        if (!Objects.isNull(tag.color))
            this.color = tag.color;
    }
}
