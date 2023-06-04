package commons;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "subtask")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Subtask implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Integer cardId;
    private String title;
    private Boolean status;
    private Integer priority;

    public Subtask(Integer id, Integer cardId, String title, Boolean status){
        this.id=id;
        this.cardId=cardId;
        this.title=title;
        this.status=status;
    }

    public void update(Subtask subtask) {
        if (!Objects.isNull(subtask.title)) {
            this.title = subtask.title;
        }
        if (!Objects.isNull(subtask.status)) {
            this.status = subtask.status;
        }
        if (!Objects.isNull(subtask.priority)) {
            this.priority = subtask.priority;
        }
    }
}
