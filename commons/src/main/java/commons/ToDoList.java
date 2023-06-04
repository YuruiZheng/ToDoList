package commons;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;


@Entity
@Table(name = "list")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ToDoList implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @JoinColumn(name = "board_id")
    private Integer boardId;

    @Column(length = 63)
    private String title;


    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(mappedBy = "listId")
    @OrderBy("priority")
    private List<Card> cards;

    public ToDoList(Integer id, Integer boardId, String title) {
        this.id = id;
        this.boardId = boardId;
        this.title = title;

    }

    public void updateList(ToDoList list) {
        if (!Objects.isNull(list.getTitle())) this.title = list.getTitle();
    }

}
