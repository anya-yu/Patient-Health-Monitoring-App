# CSE535Project1

1. Imagine you are new to the programming world and not proficient enough in coding. But, you have a brilliant idea where you want to develop a context-sensing application like Project 1.  You come across the Heath-Dev paper and want it to build your application. Specify what Specifications you should provide to the Health-Dev framework to develop the code ideally.

To develop this code as a new programmer who is not proficient in coding, I would create three kinds of specifications: sensor, network, and smartphone. Foe sensor specifications, I would specify the sensor type such as accelerometer or optical sensors, platform information like Android model or version, internal communciation protocols between sensors and processing algorithms, data flow from raw sensor data inputs to algorithms that would process the data, and order of operations for data processing. For network specifications, I would specify connectivity such as how data is handled to upload data to a database for storage and specify how data would be managed locally. For smartphone specifications, I would specify user interface components like how heart rate and respiratory rate is displayed, how users will input systems, and control elements like starting and stopping monitoring. I would also specify a user feedback mechanism, how users will be notified of vital signs passing a standard threshold. Lastly, I would specify how collected data will be visualized in graphical representations.

2. In Project 1 you have stored the user’s symptoms data in the local server. Using the bHealthy application suite how can you provide feedback to the user and develop a novel application to improve context sensing and use that to generate the model of the user?

Similar to how bHealthy uses ECG and EEG to assess mental and physical health, I would use ECG and accelerometer sensors to measure real-time physiological data to assess user conditions like stress, relaxation, and physical exertion and provide immediate feedback such as exercise recommendations or offer a sleep schedule. I could also impelement an assessment application to determine the user's mental and physical health that analyze changes in heart or respiratory rate to detect abnormal patterns, like bHealthy's Emotiv's Affectiv algorithm. Using this analysis, I would suggest training activities like exercise routines or breathing exercises like how the PETpeeves app encourages physical activity to improve health. Lastly, an wellness report can summarize all findings (abnormal vital signs and wellness scores) and recommendations into one centralized document to provide the user with insights of their health trends. This approach improves context-sensing by analyzing user vitals data and user inputs to produce a comprehensive understanding of the user's overall health. For instance, if the user reports that they feel fatigued, the app can automate vital sign measurement and user's previous health history to communicate possible health concerns, solutions, and connect them with nearby healthcare professionals. 

3. A common assumption is mobile computing is mostly about app development. After completing Project 1 and reading both papers, have your views changed? If yes, what do you think mobile computing is about and why? If no, please explain why you still think mobile computing is mostly about app development, providing examples to support your viewpoint.

After completing Project 1 and reading both papers, my view of mobile computing has changed completely. Prior to starting this course, I believed mobile computing was purely about app development for mobile devices instead of desktop or web platforms. With insights from this project as well as recent course material, I've learned the mobile computing centralizes instant gratification and immediate user feedback. It's interesting to see that user experience is significantly different from desktop applications. Mobile computing relies on user interaction through hardware such as measuring heart rate visually or respiratory rate physically. In addition to these, mobile computing can also use haptic and audio feedback.