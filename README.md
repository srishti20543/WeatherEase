# MC_A2

### Weather App Implementation

This readme provides an overview of the implementation details of the Weather App. The app allows users to retrieve weather data for a specific date in Istanbul, Turkey, either from an API or from a local database.

### Overview

The Weather App is built using Kotlin and Jetpack Compose for the UI. It follows the MVVM (Model-View-ViewModel) architecture pattern, separating concerns into data models, UI components, and business logic.

### Components

1. **MainActivity**: This is the entry point of the application. It initializes the ViewModel and the database, and sets up the Compose UI.

2. **WeatherViewModel**: The ViewModel manages UI-related data and communicates with the repository (WeatherDao) to fetch or store weather data. It also contains business logic for validating dates and handling network requests.

3. **Weather**: This data class represents a weather entry, containing information such as the date, maximum temperature, and minimum temperature.

4. **WeatherDao**: The Data Access Object (DAO) provides methods for accessing and manipulating weather data in the local database using Room.

5. **WeatherDatabase**: This class extends RoomDatabase and serves as the database holder. It provides the DAO instance to the repository.

6. **WeatherApiService**: This interface defines Retrofit HTTP methods for fetching weather data from the API.

7. **RetrofitHelper**: This object provides a Retrofit instance with the base URL and GsonConverterFactory configured.

### UI

The UI is implemented using Jetpack Compose, a modern toolkit for building native Android UI. The main screen of the app allows users to input a date in the format "YYYY-MM-DD" and fetch weather data for that date. If the date is valid, the app displays the maximum and minimum temperatures for that date. If the date is invalid or the data is not available, appropriate error messages are displayed.

### Data Fetching

Weather data is fetched either from the API or from the local database based on network connectivity and data availability. If the device is connected to the internet, the app fetches data from the API. If not, it checks if the date is in the future. If the date is in the future, it retrieves average temperature data from the local database. If the date is not in the future and data is not available in the database, an error message is displayed.

### Network Connectivity

The app checks for network connectivity using the ConnectivityManager. It determines whether the device has internet connectivity and whether it is connected via Wi-Fi, cellular, or Ethernet.

### Functionality

1. **MainActivity.onCreate()**:
   - Initializes the database and ViewModel, setting up the app's content with Jetpack Compose.
   - Centralizes the setup of essential components for the app's functionality.
   - Serves as the entry point for launching the weather application.

2. **WeatherApp() Composable Function**:
   - Constructs the UI layout using Jetpack Compose, including input fields and buttons for user interaction.
   - Facilitates user interaction by handling input changes and button clicks.
   - Communicates with `fetchWeather()` to retrieve and display weather data.

3. **fetchWeather() Function**:
   - Retrieves weather data from an API or local database based on the provided date.
   - Manages network requests, database operations, and error handling asynchronously.
   - Ensures seamless weather data retrieval and updates for the user interface.

4. **Weather Entity and WeatherViewModel**:
   - Defines the structure of weather data and provides ViewModel methods for database interactions.
   - Acts as an intermediary between the UI and data layer, facilitating data retrieval and updates.
   - Ensures separation of concerns by encapsulating data-related logic within the ViewModel.

5. **WeatherDao**:
   - Declares methods for performing CRUD operations on the weather data stored in the database.
   - Integrates with Room Persistence Library to execute database queries and transactions.
   - Provides a clean and structured API for accessing and modifying weather data.

6. **WeatherApiService Interface**:
   - Specifies the API endpoints and request parameters for fetching weather data via Retrofit.
   - Defines a contract for making network requests and receiving responses from the weather API.
   - Enables seamless integration with Retrofit to communicate with the external weather service.

7. **WeatherDatabase**:
   - Represents the local SQLite database using Room Persistence Library, providing data storage for the weather app.
   - Offers a centralized repository for managing weather data storage and retrieval.
   - Implements the Singleton pattern to ensure a single instance of the database throughout the app's lifecycle.

8. **Utility Functions** (`validateDate()`, `isDateInFuture()`, `getFutureDate()`, `isNetworkConnected()`, `RetrofitHelper`):
   - Provides essential utility functionalities such as date validation, network connectivity checks, and Retrofit setup.
   - Facilitates seamless data processing, error handling, and network communication within the weather application.
   - Enhances the app's robustness, efficiency, and user experience through reliable utility functions.

### Dependencies

- Jetpack Compose: For building the modern UI.
- Retrofit: For making network requests.
- Room Database: For local data storage and retrieval.
- Gson: For JSON parsing.

### Conclusion

The Weather App provides a simple yet effective way to retrieve weather data for a specific date in Istanbul, Turkey. It demonstrates the use of modern Android development techniques, including Jetpack Compose for UI and MVVM architecture for data management.
