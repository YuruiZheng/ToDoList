package commons;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "card_tag")
@Data
@NoArgsConstructor
public class CardTag implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer cardId;
    private Integer tagId;

    public CardTag( Integer cardId,Integer tagId) {
        this.cardId = cardId;
        this.tagId = tagId;

    }

}
