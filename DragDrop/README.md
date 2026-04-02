# Drag-and-drop
Drag and Drop any object from one view to another.

## 📱 Demo

<p align="center">
  <img src="screenshots/screenshot.png" width="350"/>
</p>


### Steps
1) Implement Long Click Listeners

    imageView.setOnLongClickListener( v -> {
        ...
    })
    
2) Create `ClipItem` and `ClipData` object 
##### Old Way
    val clipItem = ClipData.Item(textMessage)
    val mimeType = arrayOf(ClipDescription.MIMETYPE_TEXT_PLAIN)
    val clipData2 = ClipData(label, mimeType, clipItem)

##### New/Simple Way
    val clipData = ClipData.newPlainText(label, textMessage)

#### Instantiate the drag shadow builder.
    View.DragShadowBuilder(binding.sivSrc)

#### Call `startDragAndDrop()` to start dragging.
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
        view.startDragAndDrop(clipData, shadowBuilder, null, 0)
    else
        view.startDrag(clipData, shadowBuilder, null, 0)

### Drag Listeners
    Implements drag listener to handle results
    view.setOnDragListener { v, event -> 
        true
    }

### Working Algorithm
    When user make a UI gesture, app recognizes as a signal to start dragging. Thus, 
    system invoke drag listeners for us and provide a drag shadow. If dragging is released onto a 
    receiving view that accepts the data (a drop target), the system will send data to target.

### Building Blocks
    -> Drag Class
    -> Drag Listeners

### Drag Shadow Builder
  Its constructor is of two types:

  1) DragShadowBuilder(View)  -> Pass the view to generate the shadow

  2) DragShadowBuilder()      -> With empty **Constuctor** either extend `View.DragShadowBuilder` 
                                   and override its method or you will get an invisible dragging.

### Drag and Drop Process
    There are four basic steps for drag and drop:
    1) Started
    2) Continuing 
    3) Dropped
    4) Ended    
