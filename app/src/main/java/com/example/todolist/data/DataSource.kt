package com.example.todolist.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object DataSource {

    val todoItems = MutableStateFlow(
        mutableListOf(
            TaskModel(
                1,
                "Вернуться из Армии",
                TaskPriority.NORMAL,
                true,
                1657141200000,
                1625432400000,
                1657314000000
            ),
            TaskModel(
                2,
                "My name is Yoshikage Kira. I’m 33 years old. My house is in the northeast section of Morioh, where all the villas are, and I am not married. I work as an employee for the Kame Yu department stores, and I get home every day by 8 PM at the latest.",
                TaskPriority.LOW,
                true,
                null,
                1657314000000,
                1657314000000
            ),
            TaskModel(
                3,
                "Доделать приложение",
                TaskPriority.NORMAL,
                false,
                null,
                1661806800000,
                1661806800000
            ),
            TaskModel(
                4,
                "Buy some berries",
                TaskPriority.NORMAL,
                true,
                null,
                1661806800000,
                1661806800000
            ),
            TaskModel(
                5,
                "Celebrate Day of Knowledge",
                TaskPriority.LOW,
                false,
                1661979600000,
                1661806800000,
                1661806800000
            ),
            TaskModel(
                6,
                "Найти работу",
                TaskPriority.URGENT,
                false,
                1664571600000,
                1661806800000,
                1661806800000
            ),
            TaskModel(
                7,
                "Buy 12 Bananas",
                TaskPriority.LOW,
                false,
                null,
                1661806800000,
                1661806800000
            ),
            TaskModel(
                8,
                "Go to park.",
                TaskPriority.NORMAL,
                true,
                1664844400000,
                1661806800000,
                1661806800000
            ),
            TaskModel(
                9,
                "Выжить",
                TaskPriority.URGENT,
                true,
                1662152400000,
                1661806800000,
                1657314000000
            ),
            TaskModel(
                10,
                "Play some games with new fancy wired gamepad with xbox logo on it",
                TaskPriority.LOW,
                false,
                null,
                1661806800000,
                1661806800000
            )
        )
    )

    private var maxId = 10

    fun addItem(item: TaskModel) {
        maxId += 1
        val list = todoItems.value.toMutableList()
        list.add(item.copy(id = maxId))
        todoItems.value = list
    }

    fun retrieveItem(id: Int): StateFlow<TaskModel>? {
        val item = todoItems.value.firstOrNull { it.id == id }
        return if (item == null) null else MutableStateFlow(item)
    }

    fun updateItem(item: TaskModel) {
        val list = todoItems.value.toMutableList()
        val updateItemIndex = list.indexOfFirst { it.id == item.id }
        if (updateItemIndex == -1) {
            addItem(item)
        } else {
            list[updateItemIndex] = item
        }
        todoItems.value = list
    }

    fun deleteItem(itemId: Int) {
        val list = todoItems.value.toMutableList()
        val itemToDeleteIndex = list.indexOfFirst { it.id == itemId }
        if (itemToDeleteIndex != -1) {
            list.removeAt(itemToDeleteIndex)
        }
        todoItems.value = list
    }
}