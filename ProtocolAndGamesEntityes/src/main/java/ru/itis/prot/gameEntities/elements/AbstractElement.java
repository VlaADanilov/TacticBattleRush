package ru.itis.prot.gameEntities.elements;


import lombok.Getter;
import lombok.Setter;
import ru.itis.prot.gameEntities.AbstractEntity;

@Setter
@Getter
public class AbstractElement extends AbstractEntity {
    protected int index;
}
