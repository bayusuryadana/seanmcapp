import MuiAppBar, { AppBarProps as MuiAppBarProps } from '@mui/material/AppBar';
import { styled } from '@mui/material/styles';
import { drawerWidth } from './constant';
import { Toolbar, IconButton, Typography } from '@mui/material';
import MenuIcon from '@mui/icons-material/Menu';
import LogoutIcon from '@mui/icons-material/Logout';

interface AppBarProps extends MuiAppBarProps {
  open?: boolean;
}
  
export const AppBar = styled(MuiAppBar, {
  shouldForwardProp: (prop) => prop !== 'open',
})<AppBarProps>(({ theme, open }) => ({
  zIndex: theme.zIndex.drawer + 1,
  transition: theme.transitions.create(['width', 'margin'], {
    easing: theme.transitions.easing.sharp,
    duration: theme.transitions.duration.leavingScreen,
  }),
  ...(open && {
  marginLeft: drawerWidth,
  width: `calc(100% - ${drawerWidth}px)`,
  transition: theme.transitions.create(['width', 'margin'], {
    easing: theme.transitions.easing.sharp,
    duration: theme.transitions.duration.enteringScreen,
  }),
  }),
}));

interface WalletAppBarProps {
  open: boolean
  toggleDrawer: () => void
  logoutHandler: () => void
}

export const WalletAppBar = (props: WalletAppBarProps) => {
  return (
    <AppBar position="absolute" open={props.open}>
      <Toolbar sx={{ pr: '24px', }}>
        <IconButton 
          edge="start" 
          color="inherit" 
          aria-label="open drawer" 
          onClick={props.toggleDrawer} 
          sx={{ marginRight: '36px', ...({open} && { display: 'true' }),}}>
          <MenuIcon />
        </IconButton>
        <Typography component="h1" variant="h6" color="inherit" noWrap sx={{ flexGrow: 1 }}>
          Seanmcwallet
        </Typography>
        <IconButton color="inherit" onClick={props.logoutHandler}>
          <LogoutIcon />
        </IconButton>
      </Toolbar>
    </AppBar>
  )
}