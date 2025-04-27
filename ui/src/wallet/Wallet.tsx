import { ThemeProvider } from '@mui/material/styles';
import { defaultTheme } from './constant';
import { useContext, useState } from 'react';
import { Navigate, Outlet } from 'react-router-dom';
import { UserContext, UserContextType } from './UserContext';
import { Box, CssBaseline, Toolbar } from '@mui/material';
import { WalletAppBar } from './AppBar';
import { WalletDrawer } from './Drawer';

export const Wallet = () => {
    const { userContext, savePassword } = useContext(UserContext) as UserContextType;
    const [open, setOpen] = useState(false);

    const toggleDrawer = () => setOpen(!open)
    const logoutHandler = () => savePassword(null)

    if (userContext != null) {
      return (
        <ThemeProvider theme={defaultTheme}>
          <Box sx={{ display: 'flex' }}>
            <CssBaseline />
            <WalletAppBar open={open} toggleDrawer={toggleDrawer} logoutHandler={logoutHandler} />
            <WalletDrawer open={open} toggleDrawer={toggleDrawer} />
            <Box component="main" sx={{
                backgroundColor: (theme) =>
                  theme.palette.mode === 'light'
                    ? theme.palette.grey[100]
                    : theme.palette.grey[900],
                flexGrow: 1,
                height: '100vh',
                overflow: 'auto',
              }}
            >
              <Toolbar />{/* this Toolbar is supposed for bufffer below the real AppBar */}
              <Outlet />
            </Box>
          </Box>
        </ThemeProvider>
      );
    } else {
      return <Navigate to="/wallet/login" />
    }
}
