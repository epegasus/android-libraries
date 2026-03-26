package com.sohaib.regret.helper

import com.sohaib.regret.enums.CaseType
import com.sohaib.regret.interfaces.RegretListener

class Regret(private val listener: RegretListener) {

    private val undoRedoList: UndoRedoList = UndoRedoList()

    init {
        updateCanDoListener()
    }

    /**
     * @param key          an identifier for the values
     * @param currentValue the current value
     * @param newValue     the new value
     */
    fun add(key: CaseType, currentValue: Any, newValue: Any) {
        undoRedoList.add(key, currentValue, newValue)
        updateCanDoListener()
    }

    /**
     * @return the current value
     */
    val current: Action
        get() = undoRedoList.current

    /**
     * Returns the previous key-value pair via the callback onDo() in [RegretListener]
     */
    fun undo() {
        val action = undoRedoList.undo()
        updateDoListener(action)
        updateCanDoListener()
    }

    /**
     * Returns the next key-value pair via the callback onDo() in [RegretListener]
     */
    fun redo() {
        val action = undoRedoList.redo()
        updateDoListener(action)
        updateCanDoListener()
    }

    /**
     * @return true if a previous-element exists, else false
     */
    fun canUndo(): Boolean {
        return undoRedoList.canUndo()
    }

    /**
     * @return true if a next-element exists, else false
     */
    fun canRedo(): Boolean {
        return undoRedoList.canRedo()
    }

    /**
     * @return true if the collection is empty else false
     */
    val isEmpty: Boolean
        get() = undoRedoList.isEmpty

    /**
     * Deletes all elements in the collection
     */
    fun clear() {
        undoRedoList.clear()
        updateCanDoListener()
    }

    /**
     * @return the amount of elements in the list
     */
    val size: Int
        get() = undoRedoList.size

    override fun toString(): String {
        return undoRedoList.toString()
    }

    private fun updateCanDoListener() {
        listener.onCanDo(undoRedoList.canUndo(), undoRedoList.canRedo())
    }

    private fun updateDoListener(action: Action?) {
        val key = action!!.key
        val value = action.value
        listener.onDo(key, value)
    }
}