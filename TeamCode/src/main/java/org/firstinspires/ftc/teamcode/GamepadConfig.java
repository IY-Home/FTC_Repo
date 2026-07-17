package org.firstinspires.ftc.teamcode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Function;

import com.qualcomm.robotcore.hardware.Gamepad;

public class GamepadConfig {
    private final ArrayList<KeyMap> maps;

    public interface KeyMap {
        void executeActions();
    }

    public interface Pressable {
        boolean isPressed(Gamepad gamepad);
        boolean wasJustPressed(Gamepad gamepad);
        boolean wasJustReleased(Gamepad gamepad);
    }

    public enum Button implements Pressable {
        A(gamepad -> gamepad.a),
        B(gamepad -> gamepad.b),
        X(gamepad -> gamepad.x),
        Y(gamepad -> gamepad.y),
        LEFT_BUMPER(gamepad -> gamepad.left_bumper),
        RIGHT_BUMPER(gamepad -> gamepad.right_bumper),
        LEFT_STICK_BUTTON(gamepad -> gamepad.left_stick_button),
        RIGHT_STICK_BUTTON(gamepad -> gamepad.right_stick_button),
        DPAD_UP(gamepad -> gamepad.dpad_up),
        DPAD_DOWN(gamepad -> gamepad.dpad_down),
        DPAD_LEFT(gamepad -> gamepad.dpad_left),
        DPAD_RIGHT(gamepad -> gamepad.dpad_right),
        START(gamepad -> gamepad.start),
        BACK(gamepad -> gamepad.back),
        OPTIONS(gamepad -> gamepad.options),
        GUIDE(gamepad -> gamepad.guide),
        TOUCHPAD(gamepad -> gamepad.touchpad);

        private final Function<Gamepad, Boolean> isPressed;

        private final ConcurrentHashMap<Gamepad, Boolean> alreadyPressed;
        private final ConcurrentHashMap<Gamepad, Boolean> alreadyReleased;

        Button(Function<Gamepad, Boolean> isPressed) {
            this.isPressed = isPressed;
            this.alreadyPressed = new ConcurrentHashMap<>();
            this.alreadyReleased = new ConcurrentHashMap<>();
        }

        @Override
        public boolean isPressed(Gamepad gamepad) {
            return isPressed.apply(gamepad);
        }

        @Override
        public boolean wasJustPressed(Gamepad gamepad) {
            this.alreadyPressed.putIfAbsent(gamepad, false);
            if (!this.isPressed(gamepad)) {
                this.alreadyPressed.put(gamepad, false);
                return false;
            }
            if (Boolean.TRUE.equals(this.alreadyPressed.get(gamepad))) return false;
            this.alreadyPressed.put(gamepad, true);
            return true;
        }
        @Override
        public boolean wasJustReleased(Gamepad gamepad) {
            this.alreadyReleased.putIfAbsent(gamepad, false);
            if (this.isPressed(gamepad)) {
                this.alreadyReleased.put(gamepad, false);
                return false;
            }
            if (Boolean.TRUE.equals(this.alreadyReleased.get(gamepad))) return false;
            this.alreadyReleased.put(gamepad, true);
            return true;
        }
    }

    public enum Axis implements Pressable {
        LEFT_STICK_X(gamepad -> gamepad.left_stick_x),
        LEFT_STICK_Y(gamepad -> gamepad.left_stick_y),
        RIGHT_STICK_X(gamepad -> gamepad.right_stick_x),
        RIGHT_STICK_Y(gamepad -> gamepad.right_stick_y),
        LEFT_TRIGGER(gamepad -> gamepad.left_trigger),
        RIGHT_TRIGGER(gamepad -> gamepad.right_trigger);

        private final Function<Gamepad, Float> value;
        private final ConcurrentHashMap<Gamepad, Boolean> alreadyPressed;
        private final ConcurrentHashMap<Gamepad, Boolean> alreadyReleased;

        private static final float PRESS_THRESHOLD = 0.5f;

        Axis(Function<Gamepad, Float> value) {
            this.value = value;
            this.alreadyPressed = new ConcurrentHashMap<>();
            this.alreadyReleased = new ConcurrentHashMap<>();
        }

        public float getValue(Gamepad gamepad) {
            return value.apply(gamepad);
        }

        @Override
        public boolean isPressed(Gamepad gamepad) {
            return Math.abs(getValue(gamepad)) >= PRESS_THRESHOLD;
        }

        @Override
        public boolean wasJustPressed(Gamepad gamepad) {
            this.alreadyPressed.putIfAbsent(gamepad, false);
            if (!this.isPressed(gamepad)) {
                this.alreadyPressed.put(gamepad, false);
                return false;
            }
            if (Boolean.TRUE.equals(this.alreadyPressed.get(gamepad))) return false;
            this.alreadyPressed.put(gamepad, true);
            return true;
        }
        @Override
        public boolean wasJustReleased(Gamepad gamepad) {
            this.alreadyReleased.putIfAbsent(gamepad, false);
            if (this.isPressed(gamepad)) {
                this.alreadyReleased.put(gamepad, false);
                return false;
            }
            if (Boolean.TRUE.equals(this.alreadyReleased.get(gamepad))) return false;
            this.alreadyReleased.put(gamepad, true);
            return true;
        }
    }

    public static class ButtonMap implements KeyMap {
        private final Gamepad targetGamepad;

        private final HashMap<Button, Runnable> pressActions;
        private final HashMap<Button, Runnable> pressOnceActions;
        private final HashMap<Button, Runnable> releaseActions;
        private final HashMap<Button, Runnable> releaseOnceActions;

        public ButtonMap(Gamepad targetGamepad) {
            this.targetGamepad = targetGamepad;
            this.pressActions = new HashMap<>();
            this.pressOnceActions = new HashMap<>();
            this.releaseActions = new HashMap<>();
            this.releaseOnceActions = new HashMap<>();
        }

        public ButtonMap whenPressed(Button b, Runnable r) {
            pressActions.put(b, r);
            return this;
        }
        public ButtonMap onClick(Button b, Runnable r) {
            pressOnceActions.put(b, r);
            return this;
        }
        public ButtonMap whenReleased(Button b, Runnable r) {
            releaseActions.put(b, r);
            return this;
        }
        public ButtonMap onRelease(Button b, Runnable r) {
            releaseOnceActions.put(b, r);
            return this;
        }

        public ButtonMap unlink(Button b) {
            pressActions.remove(b);
            pressOnceActions.remove(b);
            return this;
        }

        @Override
        public void executeActions() {
            for (Map.Entry<Button, Runnable> entry : pressActions.entrySet()) {
                if (entry.getKey().isPressed(targetGamepad)) {
                    entry.getValue().run();
                }
            }
            for (Map.Entry<Button, Runnable> entry : pressOnceActions.entrySet()) {
                if (entry.getKey().wasJustPressed(targetGamepad)) {
                    entry.getValue().run();
                }
            }
            for (Map.Entry<Button, Runnable> entry : releaseActions.entrySet()) {
                if (!entry.getKey().isPressed(targetGamepad)) {
                    entry.getValue().run();
                }
            }
            for (Map.Entry<Button, Runnable> entry : releaseOnceActions.entrySet()) {
                if (entry.getKey().wasJustReleased(targetGamepad)) {
                    entry.getValue().run();
                }
            }
        }
    }
    public static class AxisMap implements KeyMap {
        private final Gamepad targetGamepad;
        private final HashMap<Axis, Consumer<Float>> moveActions;

        private final HashMap<Axis, Runnable> pressActions;
        private final HashMap<Axis, Runnable> pressOnceActions;
        private final HashMap<Axis, Runnable> releaseActions;
        private final HashMap<Axis, Runnable> releaseOnceActions;

        public AxisMap(Gamepad targetGamepad) {
            this.targetGamepad = targetGamepad;
            this.pressActions = new HashMap<>();
            this.pressOnceActions = new HashMap<>();
            this.moveActions = new HashMap<>();
            this.releaseActions = new HashMap<>();
            this.releaseOnceActions = new HashMap<>();
        }

        public AxisMap track(Axis a, Consumer<Float> r) {
            moveActions.put(a, r);
            return this;
        }
        public AxisMap whenPressed(Axis a, Runnable r) {
            pressActions.put(a, r);
            return this;
        }
        public AxisMap onClick(Axis a, Runnable r) {
            pressOnceActions.put(a, r);
            return this;
        }
        public AxisMap whenReleased(Axis a, Runnable r) {
            releaseActions.put(a, r);
            return this;
        }
        public AxisMap onRelease(Axis a, Runnable r) {
            releaseOnceActions.put(a, r);
            return this;
        }
        public AxisMap unlink(Axis a) {
            moveActions.remove(a);
            pressActions.remove(a);
            pressOnceActions.remove(a);
            return this;
        }

        @Override
        public void executeActions() {
            for (Map.Entry<Axis, Consumer<Float>> entry : moveActions.entrySet()) {
                entry.getValue().accept(-(entry.getKey().getValue(targetGamepad)));
            }
            for (Map.Entry<Axis, Runnable> entry : pressActions.entrySet()) {
                if (entry.getKey().isPressed(targetGamepad)) {
                    entry.getValue().run();
                }
            }
            for (Map.Entry<Axis, Runnable> entry : pressOnceActions.entrySet()) {
                if (entry.getKey().wasJustPressed(targetGamepad)) {
                    entry.getValue().run();
                }
            }
            for (Map.Entry<Axis, Runnable> entry : releaseActions.entrySet()) {
                if (!entry.getKey().isPressed(targetGamepad)) {
                    entry.getValue().run();
                }
            }
            for (Map.Entry<Axis, Runnable> entry : releaseOnceActions.entrySet()) {
                if (entry.getKey().wasJustReleased(targetGamepad)) {
                    entry.getValue().run();
                }
            }
        }
    }

    public GamepadConfig() {
        this.maps = new ArrayList<>();
    }

    public ButtonMap setButtons(Gamepad gamepad) {
        ButtonMap buttonMap = new ButtonMap(gamepad);
        this.maps.add(buttonMap);
        return buttonMap;
    }
    public AxisMap setAxes(Gamepad gamepad) {
        AxisMap axisMap = new AxisMap(gamepad);
        this.maps.add(axisMap);
        return axisMap;
    }

    public void executeActions() {
        for (KeyMap keyMap : maps) {
            keyMap.executeActions();
        }
    }

    public boolean isEmpty() {
        return maps.isEmpty();
    }

}
