package splashscreen;

import javax.swing.SwingUtilities;

import splashscreen.SplashScreenKit.SplashScreenWithProgress;

public class LoadingScreen 
{
	public static void main(String[] args) 
	{
		SwingUtilities.invokeLater(() -> 
		{
			SplashScreenWithProgress splash = new SplashScreenWithProgress();
			splash.showSplash();
			new SplashScreenKit.LoaderTask(splash).execute(); 
		});
	}
}	
