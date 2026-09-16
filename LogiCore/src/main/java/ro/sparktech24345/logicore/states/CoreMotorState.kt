package ro.sparktech24345.logicore.states

import ro.sparktech24345.logicore.hardware.CoreMotor

/**
 * Motor-specific state that includes run mode information.
 * Extends CoreState to support different motor control modes.
 *
 * @param value The numeric value for this state
 * @param name Descriptive name for the state
 * @param runMode The motor control mode to use when this state is applied
 */
open class CoreMotorState(value: Double, name: String, val runMode: CoreMotor.MotorRunMode) :
    CoreState(value, name)