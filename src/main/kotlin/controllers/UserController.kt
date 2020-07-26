package main.kotlin.controllers

import main.kotlin.ext.withUser
import main.kotlin.model.db.toDataClass
import main.kotlin.model.dto.EmployeeRequest
import main.kotlin.model.dto.EmployeeResponse
import main.kotlin.model.dto.ScheduleRequest
import main.kotlin.model.dto.ScheduleResponse
import main.kotlin.model.dto.UserInfo
import main.kotlin.net.Filters
import main.kotlin.net.Registry
import main.kotlin.net.Responses
import org.http4k.core.Body
import org.http4k.core.HttpHandler
import org.http4k.core.Method
import org.http4k.core.then
import org.http4k.format.Jackson.auto
import org.http4k.lens.Query
import org.http4k.lens.int
import org.http4k.lens.long
import org.http4k.lens.string
import org.http4k.routing.RoutingHttpHandler
import org.http4k.routing.bind
import org.http4k.routing.routes
import org.joda.time.DateTime

object UserController {
    private val authService = Registry.authService
    private val scheduleService = Registry.scheduleService
    private val employeeService = Registry.employeeService

    private val userInfoRequest = Body.auto<UserInfo>().toLens()

    //////////////////////////
    private val employeesResponse = Body.auto<List<EmployeeResponse>>().toLens()
    private val employeeRequest = Body.auto<EmployeeRequest>().toLens()

    private val employeeResponse = Body.auto<EmployeeResponse>().toLens()

    /////////////////////////
    private val schedulesResponse = Body.auto<List<ScheduleResponse>>().toLens()

    private val scheduleRequest = Body.auto<ScheduleRequest>().toLens()
    private val scheduleResponse = Body.auto<ScheduleResponse>().toLens()

    ////////////////////////// pagination

    val offsetLens = Query.long().defaulted("offset", 0)
    val limitLens = Query.int().defaulted("limit", 20)
    val uuidLens = Query.string().optional("uuid")
    val uuidRequiredLens = Query.string().required("uuid")

    fun routes(): RoutingHttpHandler {

        val me: HttpHandler = Filters.AuthFilter.then { request ->
            request.withUser { user ->
                authService.me(user.id.value)?.let { userInfo ->
                    userInfoRequest(userInfo, Responses.OK)
                } ?: Responses.BAD_REQUEST
            }
        }

        val employees: HttpHandler = Filters.AuthFilter.then { request ->
            request.withUser { user ->
                val offset = offsetLens(request)
                val limit = limitLens(request)

                val employees = employeeService.getEmployees(user, limit, offset)

                employeesResponse(employees, Responses.OK)
            }
        }

        val employee: HttpHandler = Filters.AuthFilter.then { request ->
            request.withUser { user ->
                val uuid = uuidRequiredLens(request)

                val employee = employeeService.findByUUID(user, uuid)?.toDataClass()

                if (employee == null) Responses.BAD_REQUEST else employeeResponse(employee, Responses.OK)
            }
        }

        val createEmployee: HttpHandler = Filters.AuthFilter.then { request ->
            request.withUser { user ->
                val fromRequest = employeeRequest(request)
                val employeeResponse = employeeService.create(
                    user,
                    DateTime(fromRequest.entryDate),
                    fromRequest.weeklyHours,
                    fromRequest.firstname,
                    fromRequest.lastname
                )

                employeeResponse(employeeResponse, Responses.OK)
            }
        }

        val schedules: HttpHandler = Filters.AuthFilter.then { request ->
            request.withUser {user ->
                val offset = offsetLens(request)
                val limit = limitLens(request)
                val employeeUUID = uuidLens(request)

                val schedules = scheduleService.getSchedules(user, employeeUUID, limit, offset)

                // val schedules = user.employees
                //     .flatMap { employee ->
                //         employee.schedules.map(Schedule::toDataClass)
                //     }

                schedulesResponse(schedules, Responses.OK)
            }
        }

        val createSchedule: HttpHandler = Filters.AuthFilter.then { request ->
            request.withUser { user ->
                val fromRequest = scheduleRequest(request)

                val emp = employeeService.findByUUID(user, fromRequest.employee)
                if (emp == null) Responses.BAD_REQUEST

                val schedule = scheduleService.create(
                    emp!!,
                    DateTime(fromRequest.scheduleStart),
                    DateTime(fromRequest.scheduleEnd)
                )

                scheduleResponse(schedule, Responses.OK)
            }
        }

        return routes(
            "/user" bind Method.GET to me,
            "/employee/list" bind Method.GET to employees,
            "/employee/single" bind Method.GET to employee,
            "/employee/create" bind Method.POST to createEmployee,
            "/schedule/list" bind Method.GET to schedules,
            "/schedule/create" bind Method.POST to createSchedule
        )
    }
}