import { Title } from './Title';
import { WalletDetail, WalletPlanned } from './model';
import AddIcon from '@mui/icons-material/Add';
import EditIcon from '@mui/icons-material/Edit';
import DeleteIcon from '@mui/icons-material/Delete';
import { Grid, IconButton, TableRow, TableHead, TableCell, TableBody, Table, Button, Popover, Box, Alert, TextField, Typography } from '@mui/material';
import ArrowLeftIcon from '@mui/icons-material/ArrowLeft';
import ArrowRightIcon from '@mui/icons-material/ArrowRight';
import { FormEvent, Fragment, useState } from 'react';
import { CellTypography } from './CellTypography';

interface DetailProps {
  date: string
  rows: WalletDetail[]
  planned: WalletPlanned
  editHandler: (row: WalletDetail) => void
  deleteHandler: (id: number) => void
  createHandler: () => void
  updateDashboard: (date: string) => void
}

export const Detail = (props: DetailProps) => {

  const [anchorEl, setAnchorEl] = useState<HTMLButtonElement | null>(null);
  const [alert, setAlert] = useState({display: 'none', text: ''})

  const dateConverter = (year: number, month: number) => {
    if (month == 0) {
      return (year-1).toString() + '12'
    } else if (month == 13) {
      return (year+1).toString() + '01'
    } else {
      if (month < 10) {
        return year.toString() + '0' + month.toString()
      } else {
        return year.toString() + month.toString()
      }
    }
  }

  const prevMonth = (date: string) => {
    const year = parseInt(date.slice(0,4))
    const month = parseInt(date.slice(4, 6)) - 1
    const newDate = dateConverter(year, month)
    props.updateDashboard(newDate)
  }

  const nextMonth = (date: string) => {
    const year = parseInt(date.slice(0,4))
    const month = parseInt(date.slice(4, 6)) + 1
    const newDate = dateConverter(year, month)
    props.updateDashboard(newDate)
  }

  const convertTitle = (date: string) => {
    const monthString = date.slice(4, 6)
    const monthName = (() => {
      switch(monthString) { 
        case '01': return 'January'
        case '02': return 'February';
        case '03': return 'March';
        case '04': return 'April';
        case '05': return 'May';
        case '06': return 'June';
        case '07': return 'July';
        case '08': return 'August';
        case '09': return 'September';
        case '10': return 'October';
        case '11': return 'November';
        case '12': return 'December';
        default: return ''; 
      } 
    })()
    return monthName+' '+date.slice(0, 4)
  }

  const handleClick = (event: React.MouseEvent<HTMLButtonElement>) => {
    setAnchorEl(event.currentTarget);
  };

  const handleClose = () => {
    setAnchorEl(null);
  };

  const open = Boolean(anchorEl);
  const id = open ? 'simple-popover' : undefined;

  const handleSubmit = (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    const data = new FormData(event.currentTarget);
    const input = data.get('dateInput')?.toString() ?? ""
    if (input.length > 6 || input.length < 6) {
      setAlert({display: 'true', text: 'Salah format goblok!'})
    } else {
      props.updateDashboard(input)
      setAlert({display: 'none', text: ''})
    }
  }

  return (
    <Fragment>
      <Grid container justifyContent={'space-between'}>
        <Grid item>
          <Title>Detail</Title>
        </Grid>
        <Grid item>
          <IconButton color='primary' size='medium' sx={{display: 'inline'}} onClick={() => prevMonth(props.date)}>
            <ArrowLeftIcon />
          </IconButton>
          <Button aria-describedby={id} variant="contained" onClick={handleClick}>
          {convertTitle(props.date)}
          </Button>
          <Popover
            id={id}
            open={open}
            anchorEl={anchorEl}
            onClose={handleClose}
            anchorOrigin={{
              vertical: 'bottom',
              horizontal: 'left',
            }}
          >
            {/* <Typography sx={{ p: 2 }}>The content of the Popover.</Typography> */}
            <Box component="form" onSubmit={handleSubmit} noValidate sx={{ mt: 1, p: 2 }}>
              <Alert id="wrong-format-alert" severity="error" sx={{ display: alert.display}}>{alert.text}</Alert>
              <TextField margin="normal" required fullWidth name="dateInput" label="Which month you want?" type="number" id="dateInput"/>
              <Button type="submit" fullWidth variant="contained" sx={{ mt: 3, mb: 2 }}>
                GO!
              </Button>
            </Box> 
          </Popover>
          <IconButton color='primary' size='medium' sx={{display: 'inline'}} onClick={() => nextMonth(props.date)}>
            <ArrowRightIcon />
          </IconButton>
        </Grid>
        <Grid item>
          <IconButton color='primary' size='small' onClick={props.createHandler}>
            <AddIcon />
          </IconButton>
        </Grid>
      </Grid>
      <Table size="small">
        <TableHead>
          <TableRow>
            <TableCell>Name</TableCell>
            <TableCell>Category</TableCell>
            <TableCell>Currency</TableCell>
            <TableCell>Account</TableCell>
            <TableCell align="right">Amount</TableCell>
            <TableCell></TableCell>
          </TableRow>
        </TableHead>
        <TableBody>
          {props.rows.map((row) => (
            <TableRow key={row.id}>
              <TableCell><CellTypography done={row.done}>{row.name}</CellTypography></TableCell>
              <TableCell><CellTypography done={row.done}>{row.category}</CellTypography></TableCell>
              <TableCell><CellTypography done={row.done}>{row.currency}</CellTypography></TableCell>
              <TableCell><CellTypography done={row.done}>{row.account}</CellTypography></TableCell>
              <TableCell align="right"><CellTypography done={row.done}>{row.amount}</CellTypography></TableCell>
              <TableCell>
                <IconButton aria-label="edit" color="primary" onClick={()=>props.editHandler(row)}>
                  <EditIcon />
                </IconButton>
                <IconButton aria-label="delete" color="secondary" onClick={()=>props.deleteHandler(row.id)}>
                  <DeleteIcon />
                </IconButton>
              </TableCell>
            </TableRow>
          ))}
        </TableBody>
      </Table>
      <Typography sx={{ p: 2 }}>
        Cash balance end of month | SGD: <b>S$ {props.planned.sgd.toLocaleString()}</b> | IDR: <b>Rp. {props.planned.idr.toLocaleString()}</b>
      </Typography>
    </Fragment>
  );
}
