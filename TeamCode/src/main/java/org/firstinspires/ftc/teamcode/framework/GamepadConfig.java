package org.firstinspires.ftc.teamcode.framework;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Function;

import com.qualcomm.robotcore.hardware.Gamepad;

public class GamepadConfig implements Runnable {
    private final ArrayList<KeyMap> maps;

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

    public static class KeyMap implements Runnable {
        private final Gamepad targetGamepad;
        private final HashMap<Axis, Consumer<Float>> moveActions;

        private final HashMap<Pressable, Runnable> pressActions;
        private final HashMap<Pressable, Runnable> pressOnceActions;
        private final HashMap<Pressable, Runnable> releaseActions;
        private final HashMap<Pressable, Runnable> releaseOnceActions;

        public KeyMap(Gamepad targetGamepad) {
            this.targetGamepad = targetGamepad;
            this.moveActions = new HashMap<>();
            this.pressActions = new HashMap<>();
            this.pressOnceActions = new HashMap<>();
            this.releaseActions = new HashMap<>();
            this.releaseOnceActions = new HashMap<>();
        }
        public KeyMap track(Axis a, Consumer<Float> r) {
            moveActions.put(a, r);
            return this;
        }
        public KeyMap whenPressed(Pressable b, Runnable r) {
            pressActions.put(b, r);
            return this;
        }
        public KeyMap onClick(Pressable b, Runnable r) {
            pressOnceActions.put(b, r);
            return this;
        }
        public KeyMap whenReleased(Pressable b, Runnable r) {
            releaseActions.put(b, r);
            return this;
        }
        public KeyMap onRelease(Pressable b, Runnable r) {
            releaseOnceActions.put(b, r);
            return this;
        }

        public KeyMap unlink(Pressable b) {
            pressActions.remove(b);
            pressOnceActions.remove(b);
            return this;
        }

        @Override
        public void run() {
            for (Map.Entry<Axis, Consumer<Float>> entry : moveActions.entrySet()) {
                entry.getValue().accept(-(entry.getKey().getValue(targetGamepad)));
            }
            for (Map.Entry<Pressable, Runnable> entry : pressActions.entrySet()) {
                if (entry.getKey().isPressed(targetGamepad)) {
                    entry.getValue().run();
                }
            }
            for (Map.Entry<Pressable, Runnable> entry : pressOnceActions.entrySet()) {
                if (entry.getKey().wasJustPressed(targetGamepad)) {
                    entry.getValue().run();
                }
            }
            for (Map.Entry<Pressable, Runnable> entry : releaseActions.entrySet()) {
                if (!entry.getKey().isPressed(targetGamepad)) {
                    entry.getValue().run();
                }
            }
            for (Map.Entry<Pressable, Runnable> entry : releaseOnceActions.entrySet()) {
                if (entry.getKey().wasJustReleased(targetGamepad)) {
                    entry.getValue().run();
                }
            }
        }
    }

    public GamepadConfig() {
        this.maps = new ArrayList<>();
    }

    public KeyMap on(Gamepad gamepad) {
        KeyMap keyMap = new KeyMap(gamepad);
        this.maps.add(keyMap);
        return keyMap;
    }

    @Override
    public void run() {
        for (KeyMap keyMap : maps) {
            keyMap.run();
        }
    }

    public boolean isEmpty() {
        return maps.isEmpty();
    }

}
