# Parking Coto

Private parking management system developed in Java as part of Project 2
for the EIF400 - Programming Paradigms course.

## Description

Parking Coto is an object-oriented system designed to manage vehicle entry,
parking spaces, parking tickets, payments, vehicle departures, occupancy,
and generated revenue.

The project applies object-oriented programming principles such as
abstraction, encapsulation, inheritance, polymorphism, composition,
associations, enumerations, collections, and business rules.

## Course

**EIF400 - Programming Paradigms**

**Project:** Project 2 - Object-Oriented Private Parking Management System

**Language:** Java

**Team:** Cristopher Ureña Valverde, Justin Rojas Jarquín,
and Anthony Villalobos Núñez

## Team Members

- Cristopher Ureña Valverde
- Justin Rojas Jarquín
- Anthony Villalobos Núñez

## Main Concepts

- Object-Oriented Programming
- Abstraction
- Encapsulation
- Inheritance
- Polymorphism
- Composition
- Associations
- Enumerations
- Collections
- Business Rules
- Automated Testing

## Vehicle Types

The system supports the following vehicle types:

- Automobile
- Motorcycle
- Cargo Vehicle

## Hourly Rates

| Vehicle Type | Hourly Rate |
|--------------|-------------|
| Motorcycle | ₡500 |
| Automobile | ₡900 |
| Cargo Vehicle | ₡1,500 |

Every fraction of an hour is charged as a full hour.

## Daily Maximum Rates

For stays of 10 hours or more, the following maximum rate applies per
daily period:

| Vehicle Type | Daily Maximum |
|--------------|---------------|
| Motorcycle | ₡4,000 |
| Automobile | ₡7,000 |
| Cargo Vehicle | ₡11,000 |

## Main Features

- Register vehicles
- Register parking spaces
- View available parking spaces
- Register vehicle entries
- Automatically assign compatible parking spaces
- Generate parking tickets
- View vehicles currently inside the parking lot
- Register vehicle departures
- Calculate billable hours
- Calculate the amount due
- Register payments
- Release parking spaces
- View total revenue
- View active tickets
- View occupancy by parking space type

## Project Structure

```text
parking-coto/
├── src/
│   ├── main/
│   │   └── java/
│   └── test/
│       └── java/
│
├── docs/
│   ├── uml/
│   ├── tests/
│   └── report/
│
├── pom.xml
├── README.md
└── .gitignore
