package io.dovid.multitimer.model

/**
 * Author: Umberto D'Ovidio
 * Date: 19/08/17
 * Email: umberto.dovidio@gmail.com
 * Website: http://dovid.io
 * Tutorial link : http://dovid.io
 */

class TimerEntity {

    var id: Int = 0
    var name: String? = null
    var defaultTime: Long = 0
    var expiredTime: Long = 0
    var isRunning: Boolean = false
    var isAnimating = false
    private var shouldNotify: Boolean = false
    constructor() : super() {}


    constructor(id: Int, name: String, countdownTime: Long, countdownTimeRunning: Long, currentlyRunning: Boolean, shouldNotify: Boolean) {
        this.id = id
        this.name = name
        this.defaultTime = countdownTime
        this.expiredTime = countdownTimeRunning
        this.isRunning = currentlyRunning
        this.shouldNotify = shouldNotify
    }

    fun shouldNotify(): Boolean {
        return shouldNotify
    }

    fun setShouldNotify(shouldNotify: Boolean) {
        this.shouldNotify = shouldNotify
    }

    override fun toString(): String {
        return "name: $name defaultTime: $defaultTime expiredTime: $expiredTime isRunning: $isRunning"
    }
}
