package com.game.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Data
@NoArgsConstructor
public class Frame implements Serializable {
    private final List<Operation> operations = Collections.synchronizedList(new ArrayList<>());

    public void updateFrame(Frame frame) {
        for (Operation frameOperation : frame.operations) {
            boolean hasSameName = false;
            int operationSize = operations.size();
            for (int i = 0; i < operationSize; ++i) {
                Operation thisOperation = operations.get(i);
                if (frameOperation.getName().equals(thisOperation.getName())) {  //名字相同的情况，覆盖
                    hasSameName = true;
                    if (frameOperation.isNormal()) {
                        break;
                    }
                    frameOperation.setJump(frameOperation.getJump() || thisOperation.getJump());
                    frameOperation.setFire(frameOperation.getFire() || thisOperation.getFire());
                    operations.set(i, frameOperation);
                }
            }
            if (!hasSameName) {
                operations.add(frameOperation);
            }
        }
    }
}
