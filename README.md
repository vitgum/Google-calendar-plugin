# Integration of Google Calendar
## Goal

One can use a bot to set an appointment or another event in Google Calendar.
## Usage

The plugin is called using a URL of this type ```http://plugins.miniapps.run/google-calendar?....``` For instance:
```<page version="2.0">
  <div>
    Do you wish to visit a doctor?
  </div>
 
  <navigation>
    <link pageId="http://plugins.miniapps.run/google-calendar?access_token=ya29.Gls_*************IMWrb_7I&refresh_token=1%2F*************174&time_zone=GMT%2B7&time_slot=30&work_days=12345&work_hours=09-18&on_add_event_url=ok.jps&on_exit_url=cancel.jsp">
      Make an appointment
    </link>
  </navigation>
</page>
```

## Parameters
|Parameter        |Mandatory    |Description                                             							 |
|:----------------|:-----------:|:---------------------------------------------------------------------------------------------------------------|
|access_token	  |Yes	        |Access token to Google Calendar API.										 |
|refresh_token    |Yes	        |Refresh token of the Access token to Google Calendar API.							 |
|time_zone        |Yes          |User calendar's time zone (GMT). E.g. "GMT+7".           							 |
|work_days        |Yes          |The list of working days. For example, "123456" means working days Monday to Saturday inclusive. 		 |
|work_hours       |Yes          |Working hours in the HH-HH format. For example, "09-18" means working hours from 9:00 to 18:00.		 |	
|time_slot        |Yes          |Duration of the calendar event in minutes. The value should divide 60 without a remainder (if the value is smaller than 60) and be multiple of 60 (if the value is larger than 60). For example 15, 20, 120 and so on.										 |
|on_add_event_url |Yes	        |The URL where the user should be landed after the event is successfully created.      				 |
|on_exit_url      |Yes	        |The URL where the user should be landed by user's request at any time before the event is added to the calendar.|
|locale           |No           |Language used to chat with the user. Supported values: RU, EN (the default value).				 |
