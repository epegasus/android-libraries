# Multiple TextView Undo/Redo

Lightweight Android helper for **undo/redo on TextViews**.

If you like this project, please give it a star on GitHub.

The project contains:
1. `:regret` - the reusable library (undo/redo engine + `RegretManager`)
2. `:app` - a demo app showing how to use the library

This README follows the same “project + features + usage + demo” structure style as the reference README you provided: [PhotoCollageView README](https://github.com/epegasus/PhotoCollageView/blob/master/README.md).

## Features

Undo/redo support for a single `TextView` via `RegretManager`:
- Text changes (`CaseType.TEXT`)
- Typeface changes (`CaseType.TYPEFACE`)
- Text color changes (`CaseType.TEXT_COLOR`)
- `undo()`, `redo()`
- `clear()` history reset
- `historySize` for UI/debug

Multi-TextView support:
- Create **one `RegretManager` per TextView** to keep histories independent.

## Module Setup

### Gradle dependency

In your app module (`:app`):

```kotlin
dependencies {
    implementation(project(":regret"))
}
```

### Project structure
- `settings.gradle` includes `:app` and `:regret`
- `:app` depends on `:regret` via `implementation(project(path: ':regret'))`

## Library Usage

### 1) Create a `RegretManager`

You provide a `RegretListener` to:
- be notified when undo/redo is possible (`onCanDo`)
- optionally respond to restored values (`onDo`)

`RegretManager` holds the undo/redo state internally.

### 2) Attach it to a `TextView`

Call `setView(textView)` once, then set the “previous” snapshot so the first action has a correct “from” value.

```kotlin
val regretManager = RegretManager(
    context = context, // (in the original version) - if your RegretManager signature differs, see your local code
    regretListener = object : RegretListener {
        override fun onDo(key: CaseType, value: Any?) {
            // RegretManager can update the view inside onDo (depends on your RegretManager implementation)
        }

        override fun onCanDo(canUndo: Boolean, canRedo: Boolean) {
            // Update your UI (enable/disable Undo/Redo buttons)
        }
    }
)

regretManager.setView(myTextView)
regretManager.setPreviousText(myTextView.text.toString())
regretManager.setPreviousTypeFace(myTextView.typeface)
regretManager.setPreviousTextColor(myTextView.currentTextColor)
```

### 3) Record changes + control undo/redo

Record an action by calling:
- `setNewText(newText)` (pushes `CaseType.TEXT`)
- `setNewTypeFace(typeface)` (pushes `CaseType.TYPEFACE`)
- `setNewTextColor(colorInt)` (pushes `CaseType.TEXT_COLOR`)

Then call:
- `undo()`
- `redo()`
- `clear()`

Example:

```kotlin
regretManager.setNewText("Hello")
regretManager.setNewTextColor(Color.RED.hashCode()) // use the correct Int color value for your UI

if (regretManager.canUndo()) regretManager.undo()
if (regretManager.canRedo()) regretManager.redo()

regretManager.clear()
```

### Per-TextView history (important)

Undo/redo is tracked inside each `RegretManager` instance.
So histories do not interfere **as long as you use one manager per TextView**.

## Demo App (`:app`)

The demo shows a simple integration:
- An editable `EditText` inside a card
- `Undo`, `Redo`, `Clear`
- `Change Color`, `Change Typeface`
- `Commit Text`

For multi-target behavior (when you “Add TextView as much as you want”):
you would keep a `List<RegretManager>` and switch the currently active manager based on which TextView is being edited.


https://github.com/user-attachments/assets/5cb1e6d2-17f8-4cff-885a-6c9e5f2fd06f


## Internal Notes (for reference)

Key internal classes:
- `Regret` - engine that stores undo/redo actions and triggers callbacks
- `UndoRedoList` - internal doubly-linked list + pointer for undo/redo
- `Action` - stores a `CaseType` + value for each operation
- `RegretManager` - binds undo/redo operations to one `TextView`

---

## ⭐ Please Support This Project

If this project helped you, please give us a star on GitHub. 🙏🔥

