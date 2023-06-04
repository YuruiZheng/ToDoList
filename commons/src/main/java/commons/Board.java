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
@Table(name = "board")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Board implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private String password;
    private String background;
    private String font;
    private String listBG;
    private String listF;
    private Integer defaultPreset;
    private long lastUpdate;

    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(mappedBy = "boardId")
    private List<ToDoList> toDoLists;

    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(mappedBy = "boardId")
    private List<Tag> tags;

    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(mappedBy = "boardId")
    private List<ColorPreset> colorPresets;

    public Board(Integer id, String name, String password, String background, String font,
                 String listBG, String listF, Integer defaultPreset) {
        this.id = id;
        this.name = name;
        this.password = password;
        this.background = background;
        this.font = font;
        this.listBG = listBG;
        this.listF = listF;
        this.defaultPreset = defaultPreset;
    }

    public void applyUpdates(Board board) {
        if (!Objects.isNull(board.name)) this.name = board.name;
        if (!Objects.isNull(board.password)) this.password = board.password;
        updateColors(board);
        this.lastUpdate = board.getLastUpdate() + 1;
    }

    public void updateColors(Board board) {
        if (!Objects.isNull(board.background)) this.background = board.background;
        if (!Objects.isNull(board.font)) this.font = board.font;
        if (!Objects.isNull(board.listBG)) this.listBG = board.listBG;
        if (!Objects.isNull(board.listF)) this.listF = board.listF;
        if (!Objects.isNull(board.defaultPreset)) {
            this.defaultPreset = board.defaultPreset == -1 ? null : board.defaultPreset;
        }
    }
}
