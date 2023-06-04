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
@Table(name = "card")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Card implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Integer listId;
    private String title;
    private String description;
    private String background;
    private Integer preset;
    private Integer priority;

    @LazyCollection(LazyCollectionOption.FALSE)
    @ManyToMany(cascade = CascadeType.MERGE)
    @JoinTable(name = "card_tag",
            joinColumns = {@JoinColumn(name = "cardId")},
            inverseJoinColumns = {@JoinColumn(name = "tagId")})
    private List<Tag> tags;

    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(mappedBy = "cardId")
    @OrderBy("priority")
    private List<Subtask> subtasks;



    public Card(Integer id, Integer listId, String title, String description, String background, Integer preset) {
        this.id = id;
        this.listId = listId;
        this.title = title;
        this.description = description;
        this.background = background;
        this.preset = preset;
    }

    public void update(Card card) {
        if (!Objects.isNull(card.title)) {
            this.title = card.title;
        }
        if (!Objects.isNull(card.description)) {
            this.description = card.description;
        }
        updateStyle(card);
        if (!Objects.isNull(card.priority)) {
            this.priority = card.priority;
        }
    }

    public void updateStyle(Card card) {
        if (!Objects.isNull(card.background)) {
            this.background = card.background;
        }
        if (!Objects.isNull(card.listId)) {
            this.listId = card.listId;
        }
        if (!Objects.isNull(card.preset)) {
            if(card.preset == -1) this.preset = null;
            else this.preset = card.preset;
        }
    }
}
