import { defaultTheme } from "./constant"
import CssBaseline from '@mui/material/CssBaseline';
import { AppBar } from './AppBar';
import LockOutlinedIcon from '@mui/icons-material/LockOutlined';
import { useContext, useState, FormEvent } from 'react';
import { Navigate } from "react-router-dom";
import axios from "axios";
import { Alert, Paper, Avatar, Button, ThemeProvider, Box, Toolbar, Typography, TextField } from "@mui/material";
import { UserContext, UserContextType } from "./UserContext";

export const WalletLogin = (_props: any) => {
  const  { userContext, savePassword } = useContext(UserContext) as UserContextType;
  const [alert, setAlert] = useState({display: 'none', text:''})

  const handleSubmit = (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    const data = new FormData(event.currentTarget);
    const inputPassword = data.get('password')?.toString() ?? ""

    axios.get('http://localhost:8080/api/wallet/login/' + inputPassword)
    .then((response) => {
      setAlert({ display: 'none', text: '' })
      savePassword(response.data)
    })
    .catch((error) => {
      console.log(error);
      if (error.response && error.response.status == 404) {
        setAlert({ display: 'true', text: 'Anjing gak nyambung!'})
      } else if (error.response.status == 403) {
        setAlert({ display: 'true', text: 'Salah password goblok!'})
      } else {
        setAlert({ display: 'true', text: 'Gatau nih gabisanya kenapa tot!'})
      }
    });
  };
  
  if (userContext == null) {
    return (
      <ThemeProvider theme={defaultTheme}>
        <Box sx={{ display: 'flex' }}>
          <CssBaseline />
          <AppBar position="absolute">
            <Toolbar sx={{ pr: '24px', }}>
              <Typography component="h1" variant="h6" color="inherit" noWrap sx={{ flexGrow: 1 }}>
                Seanmcwallet
              </Typography>
            </Toolbar>
          </AppBar>

          <Box component="main" sx={{
              backgroundColor: (theme) =>
                theme.palette.mode === 'light'
                  ? theme.palette.grey[100]
                  : theme.palette.grey[900],
              flexGrow: 1,
              height: '100vh',
              overflow: 'auto',
              display: 'flex',
              flexDirection: 'column',
              alignItems: 'center',
              marginTop: 8,
              paddingTop: 8,
            }}>
              <Paper sx={{p: 2, display: 'flex', flexDirection: 'column', alignItems: 'center'}}>
                <Avatar sx={{ m: 1, bgcolor: 'secondary.main' }}>
                  <LockOutlinedIcon />
                </Avatar>
                <Typography component="h1" variant="h5">
                  Sign in
                </Typography>
                <Box component="form" onSubmit={handleSubmit} noValidate sx={{ mt: 1 }}>
                  <Alert id="wrong-password-alert" severity="error" sx={{ display: alert.display}}>{alert.text}</Alert>
                  <TextField margin="normal" required fullWidth name="password" label="Password" type="password" id="password" autoComplete="current-password" />
                  <Button type="submit" fullWidth variant="contained" sx={{ mt: 3, mb: 2 }}>
                      Sign In
                  </Button>
                </Box> 
            </Paper>
          </Box>
        </Box>
      </ThemeProvider>
    );
  } else {
    return <Navigate to="/wallet" />
  }
}